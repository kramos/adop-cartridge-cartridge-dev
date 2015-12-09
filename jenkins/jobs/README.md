# Jenkins Jobs
The Jenkins jobs section of the cartridge loader will perform the following actions in this order:

 * If present, source 'generate.sh'
 * Load all XML files present in the xml directory
  * This directory can be used to store template files that 'generate.sh' can convert to XML files ready for this step
 * Load all Job DSL Groovy scripts present in the dsl directory (or any sub-directories)
  * This can be used to define pipelines that use either XML or Job DSL to define the jobs

