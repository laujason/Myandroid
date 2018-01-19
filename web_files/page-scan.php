<?php
/* Template Name: Scan */
?>

<?php
$rv = new stdClass();
global $postid;


function get16(){
    global $code;
    global $oricode;
    $code =  substr($oricode,0,16);
}
function get17(){
    global $code;
    global $oricode;
    $code =  substr($oricode,1,16);
}
function getdata(){
    global $wpdb;
    global $rv;
    global $code;
    global $post;
    $quantity = $wpdb->get_var($wpdb->prepare("SELECT SUM(quantity) FROM `orm_inventory` WHERE `item_id` = %d", $post[0]));
    $quantity = ($quantity == null? 0: $quantity);
    $rv->code = $code;
    $rv->postid = $post[0];
    $rv->productName = get_post_meta($post[0],'name',true);
    $rv->supplierName = get_post_meta($post[0],'supplier',true);
    $rv->quantity = $quantity;
}
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
    }	else {
        return false;
    }
}
function nodata(){
    global $rv;
    global $oricode;
    $rv->code = $oricode;
    $rv->productName = "";
    $rv->supplierName = "";
    $rv->quantity = 0;
    $rv->postid = "";
    $rv->exp = "";
    $rv->lot = "";

}

function trima($a, $b){
    global $oricode;
    return substr($oricode, $a, $b);
}

if ($_REQUEST['action'] == "scan") {
    $oricode = urldecode($_REQUEST['code']);
        $a10 = trima(17,2);
        $a17 = trima(27,2);
        $alot = trima(19,7);
        $b17 = trima(26,2);
        $blot = trima(19,6);
        $c17 = trima(25,2);
        $cexp = trima(27,6);
        $c10 = trima(33,2);
        $clot = trima(35,6);
        $aexp = trima(29,6);
        $bexp = trima(28,6);

        if ($a10=="10"){
            if ($a17=="17"){
                $rv->lot = $alot;
                $rv->exp =  $aexp;
            }
            if ($b17=="17"){
                $rv->lot = $blot;
                $rv->exp =  $bexp;
            }
        } else if ($c17=="17" && $c10=="10"){
            $rv->lot = $clot;
            $rv->exp = $cexp;
        } else {
             $rv->lot = "";
             $rv->exp = "";
        }

    if (substr($oricode,0,2)=="01"){
        get16();
        //echo ("01<br>");
        //echo ($code."<br>");
    } else if(substr($oricode,1,2)=="01") {
        get17();
    } else {
        $code = $oricode;
    }
    if (postexist()) {
        getdata();
    } else {
        nodata();
    }
} else if ($_REQUEST['action']=="save") {
    $code = urldecode($_REQUEST['code']);
    if (postexist()) {
        update_post_meta($post[0],'name',$_REQUEST['productName']);
        update_post_meta($post[0],'supplier',$_REQUEST['supplierName']);
    } else {
        $pid = wp_insert_post(array(
            'post_type'=>'item',
            'post_title'=>wp_strip_all_tags($_REQUEST['productName']),
            'post_status'=>'publish'
        ));
        update_post_meta($pid,'code',$code);
        update_post_meta($pid,'name',$_REQUEST['productName']);
        update_post_meta($pid,'supplier',$_REQUEST['supplierName']);
        array_push($post,$pid);
    }
    getdata();
} 

echo json_encode($rv);
exit;

?>
