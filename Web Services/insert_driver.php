<?php

include 'connection.php';

$lastName=$_POST["lastName"];
$firstName=$_POST["firstName"];
$dateOfBirth=$_POST["dateOfBirth"];
$gender=$_POST["gender"];


$query = "CALL addDriverData ('".$lastName."','".$firstName."','".$dateOfBirth."','".$gender."')";

mysqli_query($connection,$query) or die (mysqli_error());
mysqli_close($connection);
echo "base de datos actualizada con" . $lastName . $firstName . $dateOfBirth . $gender;

?>