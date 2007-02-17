<?php 
?>

<div class="sidebar">
<a href="http://www.simontuffs.com">www.simontuffs.com</a><br/>
<?php
	$page = $_GET['page'];
	foreach ($SITEMAP as $page => $array) {
		$class = "";
		foreach ($array as $key => $value) {
			$pos = strpos($key, "#");
			if (!($pos === false)) {
				echo "</div><br/><div class='sidebar header'>".$value."</div><div class='sidebar'>";
			} else {
				if (!file_exists("$page/$key.php.inc")) echo "!";
				if ($key == $file) echo "<b>";
				echo "<a href='index.php?page=$page&file=$key' $class>$value</a><br/>";
				if ($key == $file) echo "</b>";
			}
			$class = "class='indent'";
		}
	}
?>
</div>
<p/>
<div class='sidebar header'>
Documentation</div><div class='sidebar'>
<a href="http://www-128.ibm.com/developerworks/java/library/j-onejar/">IBM&nbsp;DeveloperWorks</a><br/>
<a href="/version-0.95">Version 0.95</a>
</div>
<p/>
<div style="font-size:90%; width:150px; padding:0.5em; border-style:solid; border-width:1px; border-color:blue; background-color:lightblue; line-height:1.5em; margin-right:0.5em; padding-top:0.2em;">
<h3>Quick Links:</h3>
<a href="http://prdownloads.sourceforge.net/one-jar/one-jar-sdk-0.96.jar?download">
 	<img src="download-sdk.gif" border="0" alt="download one-jar SDK" />
</a>
<p/>
<a href="http://www.sourceforge.net/projects/one-jar" target="_blank">
 	<img src="one-jar.gif" border="0" alt="one-jar at sourceforge.net" />
</a>
<p/>
<a href="http://www.simontuffs.com" target="_blank"> 
	<img src="simontuffs.com.jpg" border="0" alt="simontuffs.com" /> 
</a>
<p/>
<a href="one-jar-license.txt" target="_blank"> 
	<img src="license.jpg" border="0" alt="licensing information" /> 
</a>
<p/>
<a href="http://sourceforge.net" target="_blank"> 
	<img src="http://sourceforge.net/sflogo.php?group_id=111153&amp;type=2"	width="125" height="37" border="0" alt="SourceForge.net Logo" /> 
</a>
</div>