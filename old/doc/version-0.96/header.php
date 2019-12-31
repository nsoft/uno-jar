<?php
  	$SITEMAP = array(
  		"#title" => array(
  			"#onejar" => "One-JAR v0.96"), 
  		"introduction" => array(
  			"intro" => "Introduction", 
  			"opening" => "Opening the JAR", 
  			"options" => "Options & Properties", 
  			"gettingstarted" => "Getting Started", 
  			"manifest" => "Manifest Attributes", 
  			"native" => "Native Libraries"), 
  		"ant" => array(
  			"ant" => "Ant", 
  			"simple" => "It's Simple.  Really!"), 
  		"downloads" => array(
  			"downloads" => "Downloads", 
  			"source" => "Source Code"),
  		"support" => array(
  			"support" => "Professional Support"),
  		"documents" => array(
  			"#info" => "Information", 
  			"whatsnew" => "What's New?", 
  			"features" => "Key Features", 
  			"faq" => "FAQ", 
  			"license" => "License", 
  			"ack" => "Acknowledgements", 
  			"junit-noframes" => "Test Results"), 
  	);

  	function hyperlink($page, $file, $text, $class = "") {
  		if (!file_exists("$page/$file.php.inc")) {
  			$style="style='background-color:red; color:white'";
  		}
  		return "<a $class $style href='index.php?page=$page&amp;file=$file'>".$text."</a>";
  	}

	$PAGE = $_REQUEST['page'];
	$FILE = $_REQUEST['file'];
	$PRINT = $_REQUEST['print'];	
	$ONEPAGE = $_REQUEST['onepage'];

	if (!$SITEMAP[$PAGE]) {
		$PAGE = "introduction";
	}
	
	if (!$FILE) {
		foreach ($SITEMAP[$PAGE] as $f => $v) {
			if (strpos($f, "#") === false) {
				$FILE = $f;
				break;
			}
		}
	}
	if (!$PAGE || !$FILE) {
		$PAGE = "introduction";
		$FILE = "intro";
	}
	
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2//EN">
<html>
	<head>
		<title>Deliver Your Java Application in One-JAR&trade; !</title>
		<link rel="stylesheet" type="text/css" href="style.css" />
	</head>
<body>

<?php
	if ($ONEPAGE) {
		echo "<b><center>Deliver Your Java Application in One-JAR&trade;! Copyright 2004-2007 by P. Simon Tuffs, All Rights Reserved.  </center></b>";
	}
	if ($ONEPAGE) return;
?>

<h1>Deliver Your Java Application in One-JAR&trade;!</h1>
<b>Copyright 2004-2007 by P. Simon Tuffs, All Rights Reserved.  </b>
<br/>
<hr>
<table>
<tr>
<td valign="top">
<?php
  include("sidebar.php");
?>
</td>
<td style="padding-left:1em; border-left:solid 1px blue;" valign="top" width="100%">
