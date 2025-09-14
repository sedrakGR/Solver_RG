<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>

<meta http-equiv="Content-type" content="text/html; charset=utf-8" />

<title>SSRGT &trade;</title>

<link rel="stylesheet" type="text/css" href="static/css/gexfjs.css" />
<link rel="stylesheet" type="text/css" href="static/css/jquery-ui.css" />

<script type="text/javascript" src="static/js/lib/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
// Fallback in case JQuery CDN isn't available
	if (typeof jQuery == 'undefined') {
		document.write(unescape("%3Cscript type='text/javascript' src='js/jquery-1.7.2.min.js'%3E%3C/script%3E"));
	}
</script>
<script type="text/javascript" src="static/js/lib/jquery.mousewheel.min.js"></script>
<script type="text/javascript" src="static/js/lib/gexfjs.js"></script>
<script type="text/javascript" src="static/js/graph.js"></script>
<script type="text/javascript" src="static/js/lib/jquery-ui-1.8.16.custom.min.js"></script>

<script type="text/javascript">




</script>
</head>
    <body>
        <div id="zonecentre" class="gradient">
            <canvas id="carte" width="0" height="0"></canvas>
            <ul id="ctlzoom">
                <li>
                    <a href="#" id="zoomPlusButton" title="S'approcher"> </a>
                </li>
                <li id="zoomSliderzone">
                    <div id="zoomSlider"></div>
                </li>
                <li>
                    <a href="#" id="zoomMinusButton" title="S'éloigner"> </a>
                </li>
                <li>
                    <a href="#" id="lensButton"> </a>
                </li>
                <li>
                    <a href="#" id="edgesButton"> </a>
                </li>
                 <li>
                    <a href="#" id="skeletonButton" > </a>
                </li>
            </ul>
        </div>
        <div id="overviewzone" class="gradient">
            <canvas id="overview" width="0" height="0"></canvas>
        </div>
        <div id="leftcolumn">
            <div id="unfold">
                <a href="#" id="aUnfold" class="rightarrow"> </a>
            </div>
            <div id="leftcontent"></div>
        </div>
        <div id="titlebar">
            <div id="maintitle">
                <h1><a href="window.jsp" title="Made by Us">SSRGT Solver Home</a></h1>
            </div>
            <form id="recherche">
                <input id="searchinput" class="grey" autocomplete="off" />
                <input id="searchsubmit" type="submit" />
            </form>
        </div>
        <ul id="autocomplete">
        </ul>
        
        <div id="loader" align="center">
	        <img style=" background-color:transparent; " src="static/images/ajax-loader.gif" border="0"/>
	        <h1 align="center" style="color:white;">Please wait...</h1>
	        <h3 align="center" style="color:white;">Initializing graph visualization.</h3>
         </div>
         
       <div id="overlay" align="center">
		<h1 style="color:white;" align="center">
		Please select a layout algorithm.
		</h1>
			<table align="center">
				<tr>
				<td colspan="2" align="center">
					<input type="radio" name="layoutAlogorithm" value="FORCE_ATLAS"/>ForceAtlas
				</td>
				</tr>
				<tr>
				<td colspan="2" align="center">	
					<div id="clasp_1" class="clasp"><a href="javascript:lunchboxOpen('1');">Details...</a></div>
				</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
					<div id="lunch_1" class="lunchbox">
					<span>Home-brew layout of Gephi, it is made to spatialize Small-World / Scale-free networks. 
It is focused on quality (meaning “being useful to explore real data”) to allow a rigorous interpretation of the graph (e.g. in SNA) with the fewest biases possible, and a good 
readability even if it is slow.</span>
					</div>
					</td>
				</tr>
				<!-- <tr>
					<td colspan="2" align="center">
					<input type="radio" name="layoutAlogorithm" value="Fruchterman-Reingold"/>Fruchterman-Reingold
					</td>
				</tr>
				<tr>
				<td colspan="2" align="right">	
					<div id="clasp_2" class="clasp"><a href="javascript:lunchboxOpen('2');">Details...</a></div>
				</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
					<div id="lunch_2" class="lunchbox">It simulates the graph as a system of mass particles.</br> The nodes are the mass particles and the edges are springs between the particles.The algorithms try to minimize the energy of this physical system.</br> It has become a standard but remains very slow. 	</div>
					</td>
				</tr>	 -->
					
				<tr>
				<td colspan="2" align="center">
					<input type="radio" name="layoutAlogorithm" value="YIFANHU_MULTILEVEL"/>YifanHu Multilevel
				</td>
				</tr>
				<tr>
				<td colspan="2" align="center">	
					<div id="clasp_3" class="clasp"><a href="javascript:lunchboxOpen('3');">Details...</a></div>
				</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
					<div id="lunch_3" class="lunchbox">
					<span>It is a very fast algorithm with a good quality on large graphs. It combines a force-directed 
model with a graph coarsening technique (multilevel algorithm) to reduce the complexity. The repulsive forces on one node from a cluster of distant nodes are approximated by 
a Barnes-Hut calculation, which treats them as one super-node. It stops automatically.</span>
					</div>
					</td>
				</tr>	
					
				<!-- <tr>
					<td colspan="2" align="center">
						<input type="radio" name="layoutAlogorithm" value="Circular"/>Circular Layout
					</td>
				</tr>
				<tr>
				<td colspan="2" align="right">	
					<div id="clasp_4" class="clasp"><a href="javascript:lunchboxOpen('4');">Details...</a></div>
				</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
					<div id="lunch_4" class="lunchbox">asdasdasd</br>234234234234</div>
					</td>
				</tr>	
					
					<tr>
					<td colspan="2" align="center">
					<input type="radio" name="layoutAlogorithm" value="RadialAxis"/>Radial Axis Layout
					</td>
				</tr>
				<tr>
				<td colspan="2" align="right">	
					<div id="clasp_5" class="clasp"><a href="javascript:lunchboxOpen('5');">Details...</a></div>
				</td>
				</tr>
				<tr>
					<td  colspan="2" align="center">
					<div id="lunch_5" class="lunchbox">asdasdasd</br>asdasdasdas</div>
					</td>
				</tr>
				<tr> -->
					<td colspan="2">	
						<input type="button"  value="Continue" onclick="closeOverlay();"/>
					</td>
				</tr>
			</table>	
		</div>
    </body>
</html>