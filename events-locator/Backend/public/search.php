<?php header('Content-Type: application/json');
header("Cache-Control:max-age=120,only-if-cached,max-stale");
require_once 'functions.php';

$query = $param = "%{$_REQUEST['query']}%";;

$db = new DB_Functions();
$data = $db->search($query);
$db->close();

echo json_encode($data);