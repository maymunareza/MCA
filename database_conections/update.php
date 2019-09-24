<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array
 
//Check for Mandatory parameters
if(isset($input['username']))
{
	$username = $input['username'];
	$street_address = $input['street_address'];
	$address_pt2 	= $input['address_pt2'];
	$city 			= $input['city'];
	$zip_code 		= $input['zip_code'];
	$email	 		= $input['email'];
	$home_num 		= $input['home_num'];
	$cell_num 		= $input['cell_num'];
	

	//Query to register new user
	$updateQuery  = "UPDATE members SET street_address = ?, address_pt2 = ?, city = ?, zip_code = ?, email = ?, home_num = ?, cell_num = ? WHERE member_id = ?";
	if($stmt = $con->prepare($updateQuery))
	{
		$stmt->bind_param("ssssssss", $street_address, $address_pt2, $city, $zip_code, $email, $home_num, $cell_num, $username);
		$stmt->execute();
		$response["status"] = 0;
		$response["message"] = "Info updated";
		$stmt->close();
	}
	
}
else
{
	$response["status"] = 2;
	$response["message"] = "Missing mandatory parameters";
}
echo json_encode($response);
?>