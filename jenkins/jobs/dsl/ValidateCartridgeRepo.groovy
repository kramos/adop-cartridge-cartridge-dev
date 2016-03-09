// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"

// Jobs
def createValidateCartridgeRepoJob = freeStyleJob(projectFolderName + "/ValidateCartridgeRepo")
 
 // Setup Job 
 createValidateCartridgeRepoJob.with{
    parameters{
            stringParam("CARTRIDGE_REPO","ssh://jenkins@gerrit:29418/my-new-cartridge","Git URL of the cartridge you want to validate.")
            stringParam("CARTRIDGE_SDK_VERSION","1.0","Cartridge SDK version specification to validate against.")
    }
    environmentVariables {
        env('WORKSPACE_NAME',workspaceFolderName)
        env('PROJECT_NAME',projectFolderName)
    }
    scm {
            git{
                remote{
                    name("origin")
                    url('${CARTRIDGE_REPO}')
                    credentials("adop-jenkins-master")
                }
                branch("*/master")
            }
    }
    wrappers {
        preBuildCleanup()
        injectPasswords()
        maskPasswords()
        sshAgent("adop-jenkins-master")
    }
    steps {
        shell('''#!/bin/bash -e

echo
echo

# Checking for SDK version
if [ "$CARTRIDGE_SDK_VERSION" != "1.0" ]; then
  echo Sorry, CARTRIDGE_SDK_VERSION version $CARTRIDGE_SDK_VERSION is not supported by this job
  exit 1
fi

# Checking for existence of files
EXPECTEDFILES="README.md metadata.cartridge src/urls.txt"
for var in ${EXPECTEDFILES}
do

  if [ -f "${var}" ]; then
    echo "Pass: file ${var} exists."
  else
    echo "Fail: file ${var} does not exist."
    exit 1
  fi
done

# Checking for existence of directories
EXPECTEDDIRS="infra jenkins jenkins/jobs jenkins/jobs/dsl jenkins/jobs/xml src .git"
for var in ${EXPECTEDDIRS}
do

  if [ -d "${var}" ]; then
    echo "Pass: directory ${var} exists."
  else
    echo "Fail: directory ${var} does not exist."
    exit 1
  fi
done

# Checking for existence of Jenkins job configs
GCODE=0
cd ${WORKSPACE}/jenkins/jobs/dsl
if ls -la | awk '{ print $9}' | grep .groovy; then
GCODE=1
fi

XCODE=0
cd ${WORKSPACE}/jenkins/jobs/xml
if ls -la | awk '{ print $9}' | grep .xml; then
XCODE=1
fi

if [ $GCODE -eq 1 ]; then
	echo "Pass: Jenkins job (Groovy) config exists."
elif [ $GCODE -eq 0 ] && [ $XCODE -eq 1 ]; then
	echo "Pass: Jenkins job (XML) config exists."
	echo "Note: It is recommended that Groovy is used in favour of XML."
else
	echo "Fail: Jenkins job configs do not exist."
	exit 1
fi


echo
echo PASSED!
echo
	     ''')
	}
    
 }
