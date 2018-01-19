<?php

include(get_template_directory().'/include/ef_functions.php');

if ( function_exists('register_sidebar') )  {
//    register_sidebars(2, array('name'=>'%d'));       
}

/* Disable the Admin Bar. */
// add_filter( 'show_admin_bar', '__return_false' );

function orm_hide_admin_bar_settings() {
?>
	<style type="text/css">
		.show-admin-bar {
			display: none;
		}
	</style>
<?php
}

function orm_disable_admin_bar() {
    add_filter( 'show_admin_bar', '__return_false' );
    add_action( 'admin_print_scripts-profile.php', 
         'orm_hide_admin_bar_settings' );
}
$user = wp_get_current_user();
if ($user->ID!=1) {
add_action( 'init', 'orm_disable_admin_bar' , 9 );
}
load_theme_textdomain('ormedia',get_bloginfo('stylesheet_directory'));


add_action('wp_logout','go_home');
function go_home(){
	$redirect_to = !empty( $_REQUEST['redirect_to'] ) ? $_REQUEST['redirect_to'] : 'wp-login.php?loggedout=true';		
  wp_redirect($redirect_to );
  exit();
}

// This theme uses wp_nav_menu() in one location.
register_nav_menus( array(
	'primary' => 'Primary Navigation'
) );
add_theme_support( 'post-thumbnails' ); 


?>
