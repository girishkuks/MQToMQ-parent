 #!/bin/bash
 # This script deploys BAR files, stop & start Integration Server
 # Input Parameters
 # --e Integration Server Name - The name of the Integration Server
 # --k Application Name - Integration Application Name
 # --b BrokerName - The Name of the Integration Broker
 # --c command - deploy or undeploy
 # --v version - Bar File version 
 # Deploy bar in order as per bom.csv file
 iib_server=
 iib_app=
 iib_broker=
 command=
 iib_bar_version=
 iib_home="/app/iib/iib-10.0.0.4" 
 
# Do user input function

 function deployApp() {
 #Deploys a bar file  
 . ${iib_home}/server/bin/mqsiprofile
 ${iib_home}/server/bin/mqsideploy -e "${iib_server}" "${iib_broker}" -a "${iib_app}-${iib_bar_version}.bar" 
 # Integration App deployment status
 result=$?

 if [[ $result -ne 0 ]]
 then
 echo "${iib_app}-${iib_bar_version}.bar deploy failed."
 exit 1
 fi

 }

 function stopApp() {
  #Stop Integration App
   . ${iib_home}/server/bin/mqsiprofile
 ${iib_home}/server/bin/mqsistopmsgflow -e "${iib_server}" ${iib_broker} -k "${iib_app}"
 # api deployment status
 result=$?
       
 if [[ $result -ne 0 ]]
   then
 echo "Integration App ${iib_app} stop failed on ${iib_server}."
  exit 1
   fi
    }

 function startApp() {
   #Start  Integration App
  . ${iib_home}/server/bin/mqsiprofile
  ${iib_home}/server/bin/mqsistartmsgflow -e "${iib_server}" ${iib_broker} -k "${iib_app}"
  # api deployment status
  result=$?

  if [[ $result -ne 0 ]]
   then
     echo "Integration App ${iib_app} start failed on ${iib_server}."
    exit 1
   fi
 }

 function executemqsiCommands(){
  case $command in
    deploy)
    	deployApp
	;;
    stop)
    	stopApp
	;;
    start)
    	startApp
	;;
  esac
 }

 function parseParameters() {
 while getopts "e:k:b:c:v:" OPTION
 do
 case $OPTION in
 e)
	iib_server=$OPTARG
 ;;
 k)
 	iib_app=$OPTARG
 ;;
 b)
 	iib_broker=$OPTARG
 ;;
 c)
 	command=$OPTARG
 ;;
 v)
 	bar_version=$OPTARG
 ;; 
 esac
 done
 # ensure required params are not blank
 echo "iib_server=$iib_server,iib_app=$iib_app,iib_broker=$iib_broker,command=$command,bar_version=$bar_version"
 
 if [[ -z $iib_server ]] || [[ -z $iib_app ]] || [[ -z $iib_broker ]] || [[ -z $command ]]
 then
 exit 1
 fi
 }


 # start of main pogram
 parseParameters $@
 executemqsiCommands 
 # Deployment complete
 echo "${iib_app}-${iib_bar_version}.bar deployed successfully"
 exit 0 
