<?php

include 'connection.php';

$testArr[0] = $_GET['1'];
$testArr[1] = $_GET['2'];
$testArr[2] = $_GET['3'];


 $str = "java -cp ./src finalnetwork.ExecuteNetwork ";

foreach($testArr as $param){
 $str.=$param.' ';
}
//echo json_encode(shell_exec("cd neuralnetwork && javac src/finalnetwork/*.java && $str"));
$tempString = shell_exec("cd neuralnetwork && javac src/finalnetwork/*.java && $str");

$tempString2 = substr($tempString,0,3);


$arr = array('Percentage' => ($tempString2));
echo json_encode($arr);


?>
