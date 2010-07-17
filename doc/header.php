<?php
  	$SITEMAP = array(
  		"#title" => array(
  			"#onejar" => "One-JAR v0.97"), 
  		    "introduction" => array(
  			"intro" => "Introduction",
  			"background" => "Background", 
  			),
  	    "getting-started" => array(
  	        "quickstart" => "Quick Start", 
            "ant" => "Ant Taskdef", 
            # "one-jar-sdk" => "one-jar-sdk", 
  			# "opening" => "Opening the JAR", 
            "one-jar-appgen" => "One-Jar Appgen",
  			"sdk" => "SDK", 
  			),
        "downloads" => array(
            "downloads" => "Downloads", 
            "build" =>"Build Tree",
            "source" => "Source Code",
            "0.98_rcs" => "0.98 Pre-Release",
            ),
  		"details" => array(
            "options" => "Options & Properties", 
            "manifest" => "Manifest Attributes", 
            "resources" => "Resource Loading",
            "native" => "Native Libraries",
  		),
  		"build-tools" => array(
  		    "build-tools" => "Build Tools",
  			"ant" => "Ant",
            "ant-example" => "Ant Example",
  			"maven" => "Maven", 
  			"maven-example" => "Maven Example",
  			), 
  	    "frameworks" => array(
  	        "frameworks" => "Frameworks", 
  	        "spring" => "Spring Framework", 
  	        "guice" => "Guice", 
  	    ),
  		"more" => array(
  			"support" => "Support", 
            # "one-jar-pro" => "One-JAR Pro", 
  			),
  		"documents" => array(
  			"#info" => "More Information", 
  			"whatsnew" => "Releases    ", 
  			"features" => "Key Features", 
  			"faq" => "FAQ", 
  			"license" => "License", 
  			"ack" => "Acknowledgements", 
  			"junit-noframes" => "Test Results"
  			), 
  	);

  	function hyperlink($page, $file, $text, $class = "") {
  		if (!file_exists("$page/$file.php.inc")) {
  		    // return "<span $class>".$text."</span>";
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
		echo "<b><center>Deliver Your Java Application in One-JAR&trade;! Copyright 2004-2007 by P. Simon Tuffs, All Rights Reserved.  </b><br/><a href='http://www.simontuffs.com'>http://www.simontuffs.com</a></center>";
	}
	if ($ONEPAGE) return;
?>

<h1>Deliver Your Java Application in One-JAR&trade;!</h1>
<b>Copyright 2004-2010 by P. Simon Tuffs, All Rights Reserved.  </b> <a href="http://www.simontuffs.com">http://www.simontuffs.com</a>
<br/>

<hr/>
<table>
<tr>
<td valign="top">
<?php
  include("sidebar.php");
?>
</td>
<td style="padding-left:1em; border-left:solid 1px blue;" valign="top" width="100%">
