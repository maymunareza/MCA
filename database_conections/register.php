<?php
$response = array();
include 'db_connect.php';
include 'functions.php';
 
//Get the input request parameters
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE); //convert JSON into array


//$registration_date = date("Y-m-d H:i:s");
$ip_address = "IP PLACEHOLDER";
 
//Check for Mandatory parameters
if (isset($input['first_name']) && 
	isset($input['mid_name']) && 
	isset($input['last_name']) &&
	isset($input['street_address']) &&
	isset($input['address_pt2']) &&
	isset($input['city']) &&
	isset($input['zip_code']) &&
	isset($input['email']) &&
	isset($input['home_num']) &&
	isset($input['cell_num']) &&
	isset($input['gender']) &&
	isset($input['num_years'])){

	$first_name		= $input['first_name'];
	$mid_name 		= $input['mid_name'];
	$last_name 		= $input['last_name'];
	$street_address = $input['street_address'];
	$address_pt2 	= $input['address_pt2'];
	$city 			= $input['city'];
	$zip_code 		= $input['zip_code'];
	$email	 		= $input['email'];
	$home_num 		= $input['home_num'];
	$cell_num 		= $input['cell_num'];
	$gender			= $input['gender'];
	$num_years		= $input['num_years'];

	$member_id = getRandomString(10);
	
	//Check if user already exist
	if(1){
 
		//Query to register new user
		$insertQuery  = "INSERT INTO members (member_id, first_name, mid_name, last_name, street_address, address_pt2, city, zip_code, email, home_num, cell_num, gender) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		if($stmt = $con->prepare($insertQuery)){
			$stmt->bind_param("ssssssssssss", $member_id, $first_name, $mid_name, $last_name, $street_address, $address_pt2, $city, $zip_code, $email, $home_num, $cell_num, $gender);
			$stmt->execute();
			$response["status"] = 0;
			$response["message"] = "User created";
			$stmt->close();
		}

		// add history

		$registration_date = date("Y-m-d H:i:s");

		// test spepcific regiestration dates
		//$registration_date = "2017-07-03 14:13:38";


		$prepaid = 0;
		$i = 0;

		// get hijri year
		$getHijriQuery = "SELECT hijri_year, hijri_eng, ramadan_last FROM hijri_calendar WHERE shawwal_first < ? AND ramadan_last > ?";
		if($stmt = $con->prepare($getHijriQuery)){
			$stmt->bind_param("ss", $registration_date, $registration_date);
			$stmt->execute();

			$result = $stmt->get_result();
			$row = $result->fetch_assoc();
			$hijri_year = $row["hijri_year"];
			$hijri_eng = $row["hijri_eng"];
			$ramadan_last = $row["ramadan_last"];

			$sub_time = '-60 day';
			$sixty_before_date = date('Y-m-d H:i:s', strtotime($sub_time, strtotime($ramadan_last)));

			// TODO: check conditions for registeations = sixtybeforedaet
			if ($registration_date < $sixty_before_date) {
				$insertHQuery  = "INSERT INTO member_registration (member_id, hijri_year, membership_date, ip_address, prepaid, payment_date) VALUES (?,?,?,?,?,?)";
				if($hstmt = $con->prepare($insertHQuery)){
					$hstmt->bind_param("ssssss", $member_id, $hijri_eng, $registration_date, $ip_address, $prepaid, $registration_date);
					$hstmt->execute();
					//$response["status"] = 0;
					//$response["message"] = "User created";
					$i++;
					$hstmt->close();
				}
			} 
			else {
				$prepaid = 1;
				$next_year = $hijri_year + 1;
				$getNextHijriQuery = "SELECT hijri_year, hijri_eng, shawwal_first FROM hijri_calendar WHERE hijri_year = ?";
				if($nstmt = $con->prepare($getNextHijriQuery)){
					$nstmt->bind_param("s", $next_year);
					$nstmt->execute();

					$result = $nstmt->get_result();
					$row = $result->fetch_assoc();
					$hijri_year = $row["hijri_year"];
					$hijri_eng = $row["hijri_eng"];
					$shawwal_first = $row["shawwal_first"];


					$insertHQuery  = "INSERT INTO member_registration (member_id, hijri_year, membership_date, ip_address, prepaid, payment_date) VALUES (?,?,?,?,?,?)";
					if($hstmt = $con->prepare($insertHQuery)){
						$hstmt->bind_param("ssssss", $member_id, $hijri_eng, $shawwal_first, $ip_address, $prepaid, $registration_date);
						$hstmt->execute();
						//$response["status"] = 0;
						//$response["message"] = "User created";
						$i++;
						$hstmt->close();
					}

					$nstmt->close();

				}

				
			}


			//$response["status"] = 0;
			//$response["message"] = "User created";
			$stmt->close();
		}

		$prepaid = 1;
		while ($i < $num_years)
		{
			// get next hijri year
			$next_year = $hijri_year + $i;
			$getNextHijriQuery = "SELECT hijri_eng, shawwal_first FROM hijri_calendar WHERE hijri_year = ?";
			if($nstmt = $con->prepare($getNextHijriQuery)){
				$nstmt->bind_param("s", $next_year);
				$nstmt->execute();

				$result = $nstmt->get_result();
				$row = $result->fetch_assoc();
				//$hijri_year = $row["hijri_year"];
				$hijri_eng = $row["hijri_eng"];
				$shawwal_first = $row["shawwal_first"];

				$insertHQuery  = "INSERT INTO member_registration (member_id, hijri_year, membership_date, ip_address, prepaid, payment_date) VALUES (?,?,?,?,?,?)";
				if($stmt = $con->prepare($insertHQuery)){
					$stmt->bind_param("ssssss", $member_id, $hijri_eng, $shawwal_first, $ip_address, $prepaid, $registration_date);
					$stmt->execute();
					//$response["status"] = 0;
					//$response["message"] = "User created";
					$stmt->close();
				}

				$i++;


				$nstmt->close();
			}
			//$futureDate = date('Y-m-d H:i:s', strtotime('+1 year', strtotime($registration_date)));
			//$registration_date = date("Y-m-d H:i:s", strtotime('+1 year'));
		}


	}
	else{
		$response["status"] = 1;
		$response["message"] = "User exists";
	}
}
else{
	$response["status"] = 2;
	$response["message"] = "Missing mandatory parameters";
}
echo json_encode($response);
?>