<?php
/* Template Name: inventory */
?>

<?php
if ($_REQUEST['debug']==1){
    $lot = $_REQUEST['lot'];
    $quantity = intval($_REQUEST['quantity']);
    $targetid = $wpdb->get_var($wpdb->prepare("SELECT id FROM `orm_inventory` WHERE `lot` = %s", $lot));
        echo ($targetid);
        $wpdb->query($wpdb->prepare("UPDATE `orm_inventory` SET `quantity` = `quantity` + %d WHERE `id` = %d", $quantity, $targetid));
        if ($targetid == ""){
            echo ("no id");
        }

        
} else {

$rv = new stdClass();
$postid = $_REQUEST['postid'];
$userid = $_REQUEST['userid'];
$post;
$exp = $_REQUEST['exp'];
$lot = $_REQUEST['lot'];
$inv = $_REQUEST['inv'];
$ttp = $_REQUEST['ttp'];
$in = ($_REQUEST['method'] == "in" )? true : false;
$remark = $_REQUEST['remark'];
$inverror = false;
$giftqty = intval($_REQUEST['giftqty']);
$quantity = intval($_REQUEST['quantity']);
$productName = get_post_meta($postid,'name',true);
$remark = $_REQUEST['remark'];
$method = $_REQUEST['method'];
if ($productName==""){
    $productName = ($_REQUEST['productName']);
}

$rv->method = $method;
if ($method =="new"){
    $in = true;

    function getpost(){
        global $post;
        global $code;
        global $postid;
        $post = get_posts(array(
            'post_type'=>'item',
            'meta_key'=>'code',
            'meta_value'=>$code,
            'fields'=>'ids'
        ));
        $postid =  $post[0];
    }
    function postexist(){
        global $post;
        global $code;
        getpost();
        if (count($post)>0) {
            return true;
            
        }   else {
            return false;
            
        }
    }

    $code = urldecode($_REQUEST['code']);
    if (postexist()) {
        update_post_meta($post[0],'name',$_REQUEST['productName']);
        update_post_meta($post[0],'supplier',$_REQUEST['supplierName']);
    } else {

        $pid = wp_insert_post(array(
            'post_type'=>'item',
            'post_title'=>wp_strip_all_tags($productName),
            'post_status'=>'publish'
        ));
        update_post_meta($pid,'code',$code);
        update_post_meta($pid,'name',$productName);
        update_post_meta($pid,'supplier',$_REQUEST['supplierName']);
        array_push($post,$pid);
    }
}

if ($quantity == "" ||  $productName== "" || $quantity == 0 || $quantity =="0"){
    $inverror = true;
    $rv->msg = "數量或名稱格式不正確";
} 
    if (!$inverror){
        $inqty = $in? $quantity + $giftqty : 0 - $quantity;
        $targetid = $wpdb->get_var($wpdb->prepare("SELECT id FROM `orm_inventory` WHERE `lot` = %s", $lot));
        if (!$in && $targetid == "") {
            $inverror = true;
            $rv->msg = "該批次不存在";
        } else if ($in && $targetid == ""){
            $wpdb->insert('orm_inventory', array(
            'item_id' => $postid,
            'quantity' => $inqty,
            'exp' => $exp,
            'lot' => $lot
        ));
            $lastid = $wpdb->insert_id;
        } else {
            $lastid = $targetid;
            $result = $wpdb->query($wpdb->prepare("UPDATE `orm_inventory` SET `quantity` = `quantity` + %d WHERE `id` = %d AND `quantity` >= %d ", $inqty, $targetid, (0 - $inqty)));    
            if ($result == false || $result == 0){
                $inverror = true;
                $rv->msg = "可用數量小於消耗量";
            }
        }

        if ($in && (!$inverror)){
            $wpdb->insert('orm_invoice', array(
                'invoice' => $inv,
                'total_price' => $ttp,
                'quantity' => $quantity,
                'gift_quantity' => $giftqty
            ));
        }
        
        if (!$inverror){
            $wpdb->insert('orm_records', array(
                'item_id' => $postid,
                'user_id' => $userid,
                'quantity' => $inqty,
                'inventory_id' => $lastid,
                'remark' => $remark
            ));  
        }
        
        $rv->item_id = $postid;
        $rv->userid = $userid;
        $rv->exp = $exp;
        $rv->lot = $lot;
        $rv->inv = $inv;
        $rv->ttp = $ttp;
        $rv->remark = $remark; 
    }

$rv->quantity = $quantity;
$rv->error = $inverror;
echo json_encode($rv);
exit;

}
?>
