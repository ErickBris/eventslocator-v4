<?php header('Content-Type: application/json');
header("Cache-Control:max-age=120,only-if-cached,max-stale");
require_once 'functions.php';

$time = $_REQUEST['time'];

$db = new DB_Functions();
$data = $db->fetchAll($time);
$db->close();

echo json_encode($data);

