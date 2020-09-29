<?php

include 'connection.php';

//$driverID = '0';
$lastName=$_GET["lastName"];
$firstName=$_GET["firstName"];
$dateOfBirth=$_GET["dateOfBirth"];
$gender=$_GET["gender"];


$mysqli = $connection;
$mysqli->query("CALL `ReturnID`(@p0, '$lastName', '$firstName', '$dateOfBirth', '$gender')");
$resultado = $mysqli->query("SELECT @p0 as DriverID");
$fila = $resultado->fetch_assoc();
echo (json_encode($fila));
?>