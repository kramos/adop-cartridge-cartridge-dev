// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"

// Jobs
def createValidateCartridgeRepoJob = freeStyleJob(projectFolderName + "/ValidateCartridgeRepo")
 
 // Setup Job 
 createValidateCartridgeRepoJob.with{
    parameters{
            stringParam("CARTRIDGE_REPO","git@innersource.accenture.com:adop/cartridge-specification.git","Git URL of the cartridge you want to validate.")
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
if [ "$CARTRIDGE_SDK_VERSION" != "1.0" ]; then
  echo Sorry, CARTRIDGE_SDK_VERSION version $CARTRIDGE_SDK_VERSION is not supported by this job
  exit 1
fi

EXPECTEDFILE=metadata.cartridge
for var in ${EXPECTEDFILE}
do

  if [ -f "${var}" ]; then
    echo "Pass: file ${var} exists."
  else
    echo "Fail: file ${var} does not exist."
    exit 1
  fi
done

EXPECTEDDIRS="infra \
jenkins \
jenkins/jobs \
jenkins/jobs/dsl \
jenkins/jobs/xml \
src \
.git"
for var in ${EXPECTEDDIRS}
do

  if [ -d "${var}" ]; then
    echo "Pass: directory ${var} exists."
  else
    echo "Fail: directory ${var} does not exist."
    exit 1
  fi
done
echo
echo PASSED!
echo
	     ''')
	}
    
 }
