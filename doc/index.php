<?php 
	include("header.php");
	$page = $_GET['page'];
	$file = $_GET['file'];
	$onepage = $_GET['onepage'];
	if (!$page) {
		$page = "introduction";
		$file = "intro";
	}

	// Next/Previous page logic.  Flatten filenames into prev/curr/next (page, file, description) 
	// triples.
	foreach ($SITEMAP as $p => $a) {
		foreach ($a as $f => $d) {
			// Don't link to sentinel files.
			if (strpos($f, "#") === false) {
				$prev = $curr;
				$curr = $next;
				$next = array("page" => $p, "file" => $f, "description" => $d);
				if ($curr["page"] == $page && $curr["file"] == $file) {
					$found = true;
					break 2;
				}
			}
		}
	}
	
	if (!$found) {
		$prev = $curr;
		$curr = $next;
		$next = "";
		$found = true;
	}
	
	if ($found) {
		$NEXT = $next["description"];
		$PREV = $prev["description"];
	
		if ($NEXT) {	
			$NEXT = "<a style='text-align:right' href='index.php?page=".$next["page"]."&file=".$next["file"]."'>$NEXT&gt;&gt;</a>";
		} else {
			$NEXT = $curr["description"];
		}
		if ($PREV) {
			$PREV = "<a href='index.php?page=".$prev["page"]."&file=".$prev["file"]."'>&lt;&lt;$PREV</a>";
		} else {
			$PREV = $curr["description"];
		}
		
		if ($onepage) {
			$ONEPAGE = "<a href='index.php?page=$page&file=$file'>Multiple Pages</a>";
			$PREV = "";
			$NEXT = "";
		} else {
			$ONEPAGE = "<a href='index.php?page=$page&file=$file&onepage=true'>Single Page</a>";
		}
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
			if (strpos($file, "#") === false) {
				include("$page/$file.php.inc");
			}
		}
	}
?>

<?php include("footer.php"); ?>