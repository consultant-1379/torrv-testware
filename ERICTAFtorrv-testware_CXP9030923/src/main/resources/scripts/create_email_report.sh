
#**************************************************************
# This script will create the html file which will be rendered 
# in email as report
#**************************************************************

# Getting details of user started the build
consoleURL="${BUILD_URL}consoleText"
wget -q -O - --no-check-certificate ${consoleURL} > consoleLog.log
startedBy=`cat consoleLog.log|grep "Started by"|cut -d' ' -f 4`
if [[ ${startedBy} == "" ]]; then
   startedBy="Timer"
fi

#Getting Build Duration
consoleURLS="${BUILD_URL}"
wget -q -O - --no-check-certificate ${consoleURLS} > consoleLogs.log
echo  | sed 's/.*executing for//'
duration=`cat consoleLogs.log|grep 'executing'|sed -n '/^$/!{s/<[^>]*>//g;p;}'|sed -e '{s/^ *//g;s/ *$//g;}'`
duration=`echo $duration| sed 's/.*executing for//'`

#Testware
testWare=`echo ${testware}|sed -n '/^$/!{s/-BREAK*-/<br>/g;p;}'`

#ISOVer
if [[ "${drop}" == *::* ]];then
        isoVer=$( echo "${drop}" | sed 's/::/ /' | awk '{print $2}' )
        isoVersion=${drop}
else
        isoVer=`egrep -om 1 'isoversion=[[:digit:]]{1,2}\.[[:digit:]]{1,2}\.[[:digit:]]{1,2}' /home/lciadm100/jenkins/workspace/${JOB_NAME}/consoleLog.log | sed s'/isoversion=//'`
        isoVersion="${drop}::${isoVer}"
fi

echo  > isoversion.txt

#Replacing http with https for TAF Report
BUILD_URLS=`echo ${BUILD_URL}|sed -e "s/http/https/g"`

#setting status image
result=`cat consoleLog.log|egrep "CLUSTER DEPLOYED SUCCESSFULLY|Abort"`
if [[ ${result} == *Abort* ]]; then
    # Getting details of user that aborted the build
    abortedBy=`cat consoleLog.log|grep "Aborted by"|sed -e 's/Aborted by //g'`
    if [[ ${abortedBy} == "" ]]; then
        abortedBy="Ghost"
    fi
elif [[ ${result} == *DEPLOYED* ]]; then
  installStatusImg='<img src="https://cifwk-oss.lmera.ericsson.se/static/images/passed.png"> </img>'
else
  installStatusImg='<img src="https://cifwk-oss.lmera.ericsson.se/static/images/failed.png"> </img>'
fi

##custom functions
function getDescription() {
    testId=$1
    des=`cat ${report_f} | grep -E "id=\"TestCase_[[:digit:]]+\"" | grep ${testId} | sed 's/.*<td>\(.*\)$/\1/g'`
    echo $des
}

function printReport() {
    class=$1
    totalTC=($2)
    for ((i=0;i<${#totalTC[@]};i++)) {

         des=$(getDescription ${totalTC[$i]})
         echo    "<tr class=\"${class}\">"
         echo    "   <td>${totalTC[$i]}</td>"
         echo    "   <td>${des}</td>"
         echo    "</tr>"
    }
}




cat << EOL
<title>${JOB_NAME}</title>

<STYLE>
          body table,td, th, p, h1, h2 {
          margin:0;
          font:normal normal 100% Georgia, Serif;
          }
          h1, h2 {
          border-bottom:dotted 1px #999999;
          padding:5px;
          margin-top:10px;
          margin-bottom:10px;
          color: #000000;
          font: normal bold 130% Georgia,Serif;
          background-color:#f0f0f0;
          }
          tr.gray {
          background-color:#f0f0f0;
          }
          h2 {
          padding:5px;
          margin-top:5px;
          margin-bottom:5px;
          font: italic bold 110% Georgia,Serif;
          }
          .bg2 {
          color:black;
          background-color:#E0E0E0;
          font-size:110%
          }
          th {
          font-weight: bold;
          }
          tr, td, th {
          padding:2px;
          }
          td.test_passed {
          color:green;
          }
          td.test_failed {
          color:red;
          }
          td.test_skipped {
          color:grey;
          }
          .console {
          font: normal normal 90% Courier New, monotype;
          padding:0px;
          margin:0px;
          }
          div.content, div.header {
	  background: #ffffff;
          border: dotted
          1px #666;
          margin: 2px;
          content: 2px;
          padding: 2px;
          }
          table.border, th.border, td.border {
          border: 1px solid black;
          border-collapse:collapse;
          }
          tr.error{
       	  background-color: #FF3300;
          }
     
     	tr.warn{
     	background-color: #D2691E;
     }
     
     tr.error td.message a:visited{
     	color: #0000FF;
     }
     
     tr.excluded{
     	background-color: #99CCFF;
     }
     
     tr.passed{
     	background-color: #99CC00;
     }
     
     tr.other{
     	background-color: #FF9933;
     }
</STYLE>

<BODY>
    <div class="header">
      <!-- GENERAL INFO -->
      <h1><font color="SandyBrown"> ${phase} Deployment Status - ${isoVersion}</font></h1>
      <table>
        <tr class="gray">
          <td align="right">
              <a href="https://jenkins.lmera.ericsson.se/tor-cdb/"><img src="https://raw.githubusercontent.com/cbadke/chocolateypackages/master/jenkins/jenkins_logo.png" width=18px;height=20px;> </a>
          </td>
          <td valign="center">
            <b style="font-size: 200%;">JOB INFORMATION</b>
          </td>
        </tr>
	<tr>
          <td>Job Name:</td>
          <td>${JOB_NAME}</td>
       </tr>
	<tr>
          <td>Started by</td>
          <td>${startedBy}</td>
        </tr>
        <tr>
          <td>Job URL</td>
          <td>
            <a href="$JOB_URL" style="text-decoration:none;">Jenkins Job</a> </td>
        </tr>
        <tr>
          <td>Job Log</td>
	  <td>
	  <a href="${BUILD_URL}console" style="text-decoration:none;">Jenkins Console Log</a> </td>
        </tr>
        <tr>
          <td>Job Duration</td>
	  <td>
          <a href="${JOB_URL}buildTimeTrend" style="text-decoration:none;">${duration}</a> </td>
        </tr>

EOL
         if [[ ${result} == *Abort* ]]; then
              echo "<tr>"
              echo "<td>Aborted by </td>"
              echo "<td> ${abortedBy} </td>"
              echo "</tr>"
         fi

cat << EOL
        <tr>
          <td>Install Result</td>
          <td> ${installStatusImg} </td>
        </tr>
EOL

echo "</table>"
echo "</div>"

cat << EOL
        </table>
        </div>
        <!-- Test Report TEMPLATE -->
        <div class="content">
            <h1>TAF Execution Statistics</h1>
                <table>
                  <tr>
                    <th align=left width=1500px>Testware</th>
                    <th>TC Run</th>
                    <th>TC Passed</th>
                    <th>TC Failed</th>
                    <th>TC Skipped</th>
                    <th>Total Steps</th>
                  </tr><tr>

EOL
        overallTest=0
        overallRun=0
        overallNotRun=0
        overallPassed=0
        overallFailed=0
        overallError=0
        overallSkipped=0
        overallTCRun=0
        overallTCpassed=0
        overallTCFailed=0
        overallTCOther=0
        #report_f="target/report/*/report.htm"
	totalTCRun=[]
	totalTCPassed=[]
	totalTCFailed=[]
	totalTCOther=[]
        
        for i in target/report/*/report.htm
            do
              report_f=$i
              testSuite=`echo $i|cut -d'/' -f 3`;
              totalTest=`cat $i|grep "numsumtotal"|sed -n '/$/{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 6`;
              totalRun=`cat $i|grep "Total TCs run"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 7`;
              totalNotRun=`cat $i|grep "Total TCs not run"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 8`;
              totalPassed=`cat $i|grep "Total TCs passed"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 7`;
              totalFailed=`cat $i|grep "Total TCs failed"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 7`;
              totalError=`cat $i|grep "Total TCs error"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 7`;
              totalSkipped=`cat $i|grep "Total TCs skipped"|sed -n '/^$/!{s/<[^>]*>/ /g;p;}'|cut -d' ' -f 7`;
              totalTCRun=(`cat $i | grep -E "id=\"TestCase_[[:digit:]]+" | grep "TOR"| sed 's/.*\(TORF-[[:digit:]]*:[[:digit:]]*\).*/\1/g' | grep -E "^TOR" | sort |uniq`)
              totalTCRunNum=${#totalTCRun[@]}
              totalTCFailed=(`cat $i| grep -E "id=\"TestCase_[[:digit:]]+" | grep "TOR" | grep -E "class=\"error\"" | sed 's/.*\(TORF-[[:digit:]]*:[[:digit:]]*\).*/\1/g' | grep -E "^TOR" | sort |uniq`)
              totalTCFailedNum=${#totalTCFailed[@]}
              totalTCOther=(`cat $i| grep -E "id=\"TestCase_[[:digit:]]+" | grep "TOR" | grep -E "class=\"other\"" | sed 's/.*\(TORF-[[:digit:]]*:[[:digit:]]*\).*/\1/g' | grep -E "^TOR" | sort |uniq`)
              totalTCOtherNum=${#totalTCOther[@]}
              totalTCPassed=(`cat $i | grep -E "id=\"TestCase_[[:digit:]]+" | grep "TOR" | grep -E "class=\"passed\"" | sed 's/.*\(TORF-[[:digit:]]*:[[:digit:]]*\).*/\1/g' | grep -E "^TOR" | sort |uniq`)
              totalTCPassedNum=$((totalTCRunNum - totalTCFailedNum - totalTCOtherNum))
	      overallTest=$((overallTest + totalTest))
              overallRun=$((overallRun + totalRun))
              overallNotRun=$((overallNotRun + totalNotRun))
              overallPassed=$((overallPassed + totalPassed))
              overallFailed=$((overallFailed + totalFailed))
              overallError=$((overallError + totalError))
              overallSkipped=$((overallSkipped + totalSkipped))
              overallTCRun=$((overallTCRun + totalTCRunNum))
              overallTCFailed=$((overallTCFailed + totalTCFailedNum))
              overallTCOther=$((overallTCOther + totalTCOtherNum))
              overallTCPassed=$((overallTCRun - overallTCFailed - overallTCOther))
              echo "<tr>"
              echo "<td align=left> ${testSuite} </td>"
              echo "<td align=center> ${totalTCRunNum} </td>"
              echo "<td align=center> ${totalTCPassedNum}</td>"
              echo "<td align=center> ${totalTCFailedNum}</td>"
              echo "<td align=center> ${totalTCOtherNum}</td>"
              echo "<td align=center>${totalTest}</td>"
              echo "</tr>"
              done;
              echo "<tr><td>---------------------------------------------------------------------------------</td>"
              echo "<td>-----</td>"
              echo "<td>-----</td>"
              echo "<td>-----</td>"
              echo "<td>-----</td>"
              echo "<td>-----</td>"
              echo "</tr>"
              echo "<tr>"
              echo "<td>Grand Total</td>"
              echo "<td align=center> ${overallTCRun} </td>"
              echo "<td align=center> ${overallTCPassed}</td>"
              echo "<td align=center> ${overallTCFailed}</td>"
              echo "<td align=center> ${overallTCOther}</td>"
              echo "<td align=center>${overallTest}</td>"
              echo "</tr>"
              echo "</table><br /> </div>"
    if [[ ${buildResult} != *Abort* ]]; then
    JS_FILE="pie.js"
    PIE_CHART_HTML="generate_pie.html"
    PIE_PIC="pie_chart.jpg"
    PIE_HTML_ID='piechart'
    cat > $JS_FILE <<-EOL
       var page = require('webpage').create();
       page.viewportSize = { width: 400, height: 240 };
       page.open('${PIE_CHART_HTML}', function start(status) {
       page.evaluate(function() {
          document.body.bgColor = 'white';
        });
        var clipRect = page.evaluate(function () { return document.getElementById('${PIE_HTML_ID}').getBoundingClientRect();  });
        page.clipRect = {
            height: clipRect.height,
            left:   clipRect.left,
            width:  clipRect.width,
            height: clipRect.height
        };

        page.render('${PIE_PIC}', {format: 'jpeg', quality: '85'});
        phantom.exit();

}); 
EOL
    cat > $PIE_CHART_HTML <<-EOL
        <html>
  <head>
    <script type="text/javascript" src="./jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {

        var data = google.visualization.arrayToDataTable([
          ['Status', 'Percentage'],
          ['Passed', ${totalTCPassedNum}],
          ['Failed', ${totalTCFailedNum}],
          ['Skipped',${totalTCOtherNum}],
        ]);

        var options = {
          title: 'TORRV DV Tests',
          slices: {
              0: {color: '#99CC00'},
              1: {color: '#FF3300'},
              2: {color: '#FF9933'}
           }
        };

        var chart = new google.visualization.PieChart(document.getElementById('${PIE_HTML_ID}'));

        chart.draw(data, options);
      }
    </script>
  </head>

 <body>
    <div id="${PIE_HTML_ID}" style="width: 400px; height: 240px;"></div>
 </body>
</html>	

EOL


    phantomjs/bin/phantomjs ${JS_FILE} 
    cat <<- EOL
 	<div class="content">
         <h1>TAF Test Summary</h1>
            <img src="${PIE_PIC}" alt="test_summary_pie_chart" />
        </div>
EOL
    
    cat <<- EOL
        <div class="content">
        <h1>TAF Test Report</h1>
            <table>
             <thread><tr><th>TC id</th><th>Description</th></tr></thread>
EOL
printReport passed "${totalTCPassed[*]}"
printReport error "${totalTCFailed[*]}"
printReport other "${totalTCOther[*]}"

echo   " </table>"
echo "</div>"



cat << EOL  
        <div class="content">
        <h1>TAF Test Report</h1>
            <table>
                <tr>
                    <td> <a href="${BUILD_URLS}TAF_Report" style="text-decoration:none;"> <img src="https://cifwk-oss.lmera.ericsson.se/static/images/relatedItem.png" width=18px;height=20px;></img> &nbsp;&nbsp;View Full Report </a></td>

                
EOL

        for i in target/report/*/report.htm
                do
                  testSuites=`echo $i|cut -d'/' -f 3`
cat << EOL
                   <tr>
                      <td> <a href="${BUILD_URLS}TAF_Report/${testSuites}/index.html" style="text-decoration:none;"> <img src="https://cifwk-oss.lmera.ericsson.se/static/images/relatedItem.png" width=18px;height=20px;></img>&nbsp;&nbsp; View <font color="Salmon"> ${testSuites} </font> Report </a></td>
                     </tr>
EOL
                    done;

    fi

#Determine what server was installed so we can provide the IP for the LMS and the UI
if [[ ${JOB_NAME} =~ "LMS1" ]];
then
   lmsIP="10.59.142.4";
   uiIP="10.59.142.22";
elif [[ ${JOB_NAME} =~ "LMS2" ]];
then
   lmsIP="10.59.142.77";
   uiIP="10.59.142.51";
elif [[ ${JOB_NAME} =~ "Cloud" ]];
then
   lmsIP="ssh to ms-1 from gateway";
   uiIP="192.168.0.80";
else
   lmsIP="Unknown";
   uiIP="Unknown";
fi
    
#cat << EOL
#        </table>
#        </div>
#<div class="content">
#	<h1>Information</h1>
#        <table>
#            <tr class="gray">
#                <b><font color="LightSeaGreen">
#                <td width=650px valign="top">LMS</td>
#		</b></font>
#                <td valign="top"> ${lmsIP} </td>
#            </tr>
#            <tr><b><font color="LightSeaGreen">
#                <td width=650px valign="top">UI IP</td>
#		</b></font>
#                <td valign="top"> ${uiIP} </td>
#            </tr>
#            <tr><b><font color="LightSeaGreen">
#                <td valign="top"> Gateway hostname </td>
#                </b></font>
#                <td valign="top"> `hostname`</td>
#            </tr>
#            <tr><b><font color="LightSeaGreen">
#                <td> Gateway IP </td></b></font>
#                <td> `hostname -i`</td>
#            </tr>
#            <tr><b><font color="LightSeaGreen">
#                <td valign="top"> Workspace </td></font>
#                <td valign="top"> ${WORKSPACE}</td>
#            </tr>
#	    <tr><b><font color="LightSeaGreen">
#                <td> Drop </td></b></font>
#                <td> ${drop} </td>
#	    </tr>
#            <tr><b><font color="LightSeaGreen">
#                <td valign="top"> ISO Version </td></b></font>
#                <td valign="top"> ${isoVer} </td>
#	    </tr>
#	    <tr><b><font color="LightSeaGreen">
#  		<td> Testware Mapped</td></b></font>
#		<td> ${testWare} </td>
#	    </tr>
#	    <tr><b><font color="LightSeaGreen">
#            <td valign="top"> Packages Deployed </td></b></font>
#	    <td valign="top">
#
#EOL

	   if [[ ${deployPackage} ]];then
		packageList=$( echo ${deployPackage}| sed 's/||/ /g'  )
		for packageTmp in ${packageList}
		do
        		packageVersion=$( echo $packageTmp | sed 's/::/ /g' | awk '{print $2}' )
		        package=$( echo $packageTmp | sed 's/::/ /g' | awk '{print $1}' ) 
		        packageRevisionInfover=$( wget -q -O - --no-check-certificate "https://cifwk-oss.lmera.ericsson.se/dmt/getLatestPackageObj/?package=${package}&version=${packageVersion}" )
		        ver=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $1}' )
		        groupId=$( echo $packageRevisionInfover | sed 's/::/ /' | awk '{print $2}' )
cat << EOL
		               ${package}-${ver}<br> 
			       
EOL
		 done;
	  fi
cat << EOL
</td></tr>	
	 
</table>
</div>

EOL



