<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array
 
//Check for Mandatory parameters
if(isset($input['username']) && isset($input['password'])){
	$username = $input['username'];
	$password = $input['password'];
	$query    = "SELECT first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, admin_lvl FROM members WHERE member_id = ?";

	if($stmt = $con->prepare($query)){
		$stmt->bind_param("s",$username);
		$stmt->execute();
		$stmt->bind_result($first_name, $mid_name, $last_name, $street_address, $address_pt2, $city, $zip_code, $email, $home_num, $cell_num, $admin_lvl);
		if($stmt->fetch()){
			if(!strcmp($last_name, $password)){
				$response["status"] = 0;
				$response["message"] = "Login successful";
				$response["member_id"] 	= $username;
				$response["first_name"] = $first_name;
				$response["mid_name"] 	= $mid_name;
				$response["last_name"] 	= $last_name;
				$response["street_address"] 	= $street_address;
				$response["address_pt2"] 	= $address_pt2;
				$response["city"] 	= $city;
				$response["zip_code"] 	= $zip_code;
				$response["email"] 	= $email;
				$response["home_num"] 	= $home_num;
				$response["cell_num"] 	= $cell_num;
				$response["admin_lvl"]  = $admin_lvl;

			}
			else{
				$response["status"] = 1;
				$response["message"] = "Invalid username and password combination";
			}
		}
		else{
			$response["status"] = 1;
			$response["message"] = "Invalid username and password combination";
		}
		
		$stmt->close();
	}
}
else{
	$response["status"] = 2;
	$response["message"] = "Missing mandatory parameters";
}
//Display the JSON response
echo json_encode($response);
?>
