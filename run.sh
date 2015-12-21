
JOB="$1"
HOME_APP_DIR="/appl/emmd/source/azure-emmd-batch-jobs"
echo "Running Job $JOB"
source $HOME_APP_DIR/setenv.sh

PID=$(ps -ef|grep java|grep azure-emmd-batch-jobs | grep loader.JarLauncher|grep -v grep | awk '{print $2}')
		
 
	 	if [[ -n $PID ]]; then
			echo  azure-emmd-batch-jobs already running on process $PID
		  	echo run $JOB cancelled
		  	exit 1
		fi
			echo "Starting up on: " `uname -a`
			nohup java -Xms512m -Xmx1024m -cp "$HOME_APP_DIR/libs/guava-18.0.jar:$HOME_APP_DIR/libs/hive-exec-1.0.0.jar:$HOME_APP_DIR/target/azure-emmd-batch-jobs-1.0.0-SNAPSHOT.jar" org.springframework.boot.loader.JarLauncher "$JOB" &
			echo "Running $JOB in background!"
			

