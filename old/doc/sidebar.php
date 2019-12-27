<div class="sidebar">
<a href="http://www.sourceforge.net/projects/one-jar" target="_blank">sourceforge.net/one-jar</a>
<br/>
<a href="https://sourceforge.net/apps/wordpress/one-jar/">One-JAR Blog</a>
</div><br/>

<?php
	$first = true;
	foreach ($SITEMAP as $page => $array) {
		$class = "";
		$noindent = false;
		foreach ($array as $key => $value) {
			$pos = strpos($key, "#");
			if (!($pos === false)) {
				if (!$first) {
					echo "</div><br/>";
				}
				$first = false;
				echo "<div class='sidebar header'>".$value."</div><div class='sidebar'>";
				$noindent = true;
			} else {
				if ($key == $FILE) echo "<b>";
				echo hyperlink($page, $key, $value, $class); echo "<br/>";
				if ($key == $FILE) echo "</b>";
			}
			if (!$noindent) $class = "class='indent'";
		}
	}
?>
</div>
<br/>
<div class='sidebar header'>
Other Documentation</div><div class='sidebar'>
<a href="http://www-128.ibm.com/developerworks/java/library/j-onejar/">IBM&nbsp;DeveloperWorks</a>
<br/><a href="version-0.96/">Version 0.96</a>
<br/><a href="version-0.95/">Version 0.95</a>
</div>
<!--
<br/>
<div class="sidebar header">Quick Links</div>
<div class="sidebar">
<?=hyperlink("downloads", "downloads", "Downloads")?>
<br/><a href="http://www.simontuffs.com" target="_blank">www.simontuffs.com</a>
<br/>
<a href="http://sourceforge.net/projects/one-jar"><img src="http://sflogo.sourceforge.net/sflogo.php?group_id=111153&amp;type=10" width="80" height="15" alt="Get One-JAR(TM) at SourceForge.net. Fast, secure and Free Open Source software downloads" /></a></a>
-->
</div>