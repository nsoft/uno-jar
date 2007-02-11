<?php 
	include("header.php");
	$page = $_GET['page'];
	$file = $_GET['file'];
	$onepage = $_GET['onepage'];
	if (!$page) {
		$page = "introduction";
		$file = "intro";
	}

	// Next/Previous page logic.
	$ks = array_keys($SITEMAP);
	$kp = array_keys($SITEMAP[$page]);
	$p = array_search($page, array_keys($SITEMAP));
	$f = array_search($file, array_keys($SITEMAP[$page]));
	$next = $kp[$f+1];
	$prev = $kp[$f-1];

	
	$NEXT = $SITEMAP[$page][$next]." &gt;&gt;";
	$PREV = "&lt;&lt; ".$SITEMAP[$page][$prev];

	if (strpos($NEXT, "#nolink")) $next = false;
	if (strpos($PREV, "#nolink")) $prev = false;
		
	if ($next) {
		$NEXT = "<a style='text-align:right' href='index.php?page=$page&file=$next'>$NEXT</a>";
	} else {
		$NEXT = $SITEMAP[$page][$file];
	}
	if ($prev) {
		$PREV = "<a href='index.php?page=$page&file=$prev'>$PREV</a>";
	} else {
		$PREV = $SITEMAP[$page][$file];
	}
	if ($onepage) {
		$ONEPAGE = "<a href='index.php?page=$page&file=$file'>Multiple Pages</a>";
		$PREV = "";
		$NEXT = "";
	} else {
		$ONEPAGE = "<a href='index.php?page=$page&file=$file&onepage=true'>Single Page</a>";
	}
?>

<p/>	
<table class="navbar" width="100%"><tr><td width="33%"> <?php echo $PREV; ?> </td><td align="center" width="33%"><?php echo $ONEPAGE; ?></td><td align="right"> <?php echo $NEXT; ?> </td></tr></table>

<?php	
	if (!$onepage) {
		include("$page/$file.php.inc");
	} else {
		// Render all files in a topic as one page.
		$pages = $SITEMAP[$page];
		foreach ($pages as $file => $title) {
			if (strpos($title, "#nolink")) continue;
			include("$page/$file.php.inc");
		}
	}
?>

<?php include("footer.php"); ?>