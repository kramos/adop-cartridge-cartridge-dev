// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"

// Jobs
def publishCartridgeJob = freeStyleJob(projectFolderName + "/PublishCartridgeRepo")
 
 // Setup Job 
 publishCartridgeJob.with{
    parameters{
            stringParam("CARTRIDGE_REPO","ssh://jenkins@gerrit:29418/my-new-cartridge","Git URL of the cartridge you want to publish.")
            stringParam("TARGET_CARTRIDGE_REPO","","Git URL of the target repository where you want to push your cartridge to. Ensure you have added the Jenkins SSH key to the repository browser.")
    }
    environmentVariables {
        env('WORKSPACE_NAME',workspaceFolderName)
        env('PROJECT_NAME',projectFolderName)
    }
    scm {
            git{
                remote{
                    name("origin")
                    url('${TARGET_CARTRIDGE_REPO}')
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
        shell('''#!/bin/bash -ex

echo
echo

# Fetch all branches from local Gerrit repository for cartridge
git remote add gerrit $CARTRIDGE_REPO
git fetch gerrit

# Push all branches to remote repository
git push origin +refs/remotes/gerrit/*:refs/heads/*

set +x
echo
echo ALL FINISHED!
echo Your local cartridge has been pushed to the specified target: ${TARGET_CARTRIDGE_REPO}
echo
''')
    }
    
 }
