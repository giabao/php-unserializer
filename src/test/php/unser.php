<?php
include(dirname(__FILE__).'/data.php');

$f = fopen($dataFile, 'r');
$serialized = fread($f, filesize($dataFile));
$actual = unserialize($serialized);
fclose($f);

if($actual != $data) exit(1);
