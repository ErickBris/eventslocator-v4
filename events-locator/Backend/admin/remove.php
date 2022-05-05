<?php header('Content-Type: application/json');
require_once 'functions.php';

$id = $_REQUEST['id'];
$db = new DB_Functions();
$db->removePage($id);
$db->close();

header("Location: index.php");
die();
