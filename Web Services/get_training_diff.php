<?php

include 'connection.php';

$mysqli = $connection;
$mysqli->query("CALL `returnTrainingDiff`(@p0)");
$resultado = $mysqli->query("SELECT @p0 as Training");
$fila = $resultado->fetch_assoc();
echo ($fila["Training"]);
?>
