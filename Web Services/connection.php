<?php

$hostname='localhost';
$database='forecast';
$username='root';
$password='';

$connection=new mysqli($hostname,$username,$password,$database);
if($connection->connect_errno){
	echo "Connection error.";
}

?>