<?php
/* Template Name: supplier */
?>

<?php
if ($_REQUEST['action']=="get"){
    $rv = new stdClass();
    $sup_list = $wpdb->get_results($wpdb->prepare("SELECT * FROM `orm_supplier` WHERE id <999"));
    $rv->sup_list = $sup_list;
    echo json_encode($rv);
        
} else {

$rv = new stdClass();
$postid = $_REQUEST['postid'];
$userid = $_REQUEST['userid'];
$exp = $_REQUEST['exp'];
$lot = $_REQUEST['lot'];
$inv = $_REQUEST['inv'];
$ttp = $_REQUEST['ttp'];
$in = ($_REQUEST['method'] == "in")? true : false;
$remark = $_REQUEST['remark'];
$inverror = false;
$giftqty = intval($_REQUEST['giftqty']);
$quantity = intval($_REQUEST['quantity']);
$productName = get_post_meta($postid,'name',true);
$remark = $_REQUEST['remark'];
$rv->method = $_REQUEST['method'];
if ($quantity == "" ||  $productName== "" || $quantity == 0 || $quantity =="0"){
    $inverror = true;
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
