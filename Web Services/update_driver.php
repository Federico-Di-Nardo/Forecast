<?php

include 'connection.php';

$driverID=$_POST["driverID"];
$lastName=$_POST["lastName"];
$firstName=$_POST["firstName"];
$dateOfBirth=$_POST["dateOfBirth"];
$gender=$_POST["gender"];


$query = "CALL `updateDriverData`('$driverID', '$lastName', '$firstName', '$dateOfBirth', '$gender')";

mysqli_query($connection,$query) or die (mysqli_error());
mysqli_close($connection);
echo "base de datos actualizada con" . $lastName . $firstName . $dateOfBirth . $gender;

?>