<?php 
	include_once("header.php");

	// Next/Previous page logic.  Flatten filenames into prev/curr/next (page, file, description) 
	// triples.
	foreach ($SITEMAP as $p => $a) {
		foreach ($a as $f => $d) {
			// Don't link to sentinel files.
			if (strpos($f, "#") === false) {
				$prev = $curr;
				$curr = $next;
				$next = array("page" => $p, "file" => $f, "description" => $d);
				if ($curr["page"] == $PAGE && $curr["file"] == $FILE) {
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
			$NEXT = "<a style='text-align:right' href='index.php?page=".$next["page"]."&amp;file=".$next["file"]."'>$NEXT&gt;&gt;</a>";
		} else {
			$NEXT = $curr["description"];
		}
		if ($PREV) {
			$PREV = "<a href='index.php?page=".$prev["page"]."&amp;file=".$prev["file"]."'>&lt;&lt;$PREV</a>";
		} else {
			$PREV = $curr["description"];
		}
		
		if ($onepage) {
			// Figure out prev and next pages.
			$found = false;
			foreach ($SITEMAP as $page => $a) {
				$prevpage = $currpage;
				$currpage = $nextpage;
				$nextpage = $page;
				if ($currpage == $PAGE) {
					$found = true;
					break;
				}
			}
			if (!$found) {
				$currpage = $nextpage;
				$nextpage = "";
			}
			if ($prevpage) {
				$PREV = reset($SITEMAP[$prevpage]);
				$PREV = "<a href='index.php?page=".$prevpage."&amp;onepage=true'>&lt;&lt;$PREV</a>";
			} else {
				$PREV = reset($SITEMAP[$currpage]);
			}
			if ($nextpage) {
				$NEXT = reset($SITEMAP[$nextpage]);
				$NEXT = "<a href='index.php?page=".$nextpage."&amp;onepage=true'>$NEXT&gt;&gt;</a>";
			} else {
				$NEXT = "";
			}
			$CURR = reset($SITEMAP[$currpage]);
			$LAYOUT = "<a href='index.php?page=$PAGE&amp;file=$FILE'>$CURR</a>";
		} else {
			$LAYOUT = "<a href='index.php?page=$PAGE&amp;file=$FILE&amp;onepage=true'>Print Layout</a>";
		}
	}
	
?>

<table class="navbar" width="100%"><tr><td width="33%"> <?php echo $PREV; ?> </td><td align="center" width="33%"><?php echo $LAYOUT; ?></td><td align="right"> <?php echo $NEXT; ?> </td></tr></table>

<?php	
	if (!$onepage) {
		if (!file_exists("$PAGE/$FILE.php.inc")) {
			include("sorry.php.inc");
			return;
		}
	
		include("$PAGE/$FILE.php.inc");
	} else {
		// Render all files in a topic as one page.
		$pages = $SITEMAP[$PAGE];
		foreach ($pages as $file => $title) {
			if (strpos($file, "#") === false) {
				if (!file_exists("$PAGE/$FILE.php.inc")) {
					include("sorry.php.inc");
				} else {
					include("$PAGE/$file.php.inc");
				}
			}
		}
	}
?>

<?php include("footer.php"); ?>