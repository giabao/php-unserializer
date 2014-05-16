<?php
include(dirname(__FILE__).'/data.php');

$f = fopen($dataFile, 'w');
fwrite($f, serialize($data));
fclose($f);
