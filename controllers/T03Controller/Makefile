# Copyright 1996-2020 Cyberbotics Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

### Generic Webots Makefile system for Java controllers, modified from the original file to make sure that the clean target actually deletes the class files in the "/ca/mcgill/ecse211/project/" folder


# WEBOTS_HOME is a sine qua non condition to run this Makefile
ifndef WEBOTS_HOME
 $(error The WEBOTS_HOME environment variable is not defined.)
endif

# WEBOTS_HOME_PATH is commonly defined in the caller Makefile
# but as it is not necessary on non-windows OS, it seems safer to reconstruct it there if required
ifndef WEBOTS_HOME_PATH
 WEBOTS_HOME_PATH=$(strip $(subst % ,\ ,$(strip $(subst \,/,$(WEBOTS_HOME)))))
endif


###-----------------------------------------------------------------------------
### OS determination and OS specific commands
###-----------------------------------------------------------------------------

include $(WEBOTS_HOME_PATH)/resources/Makefile.os.include

###-----------------------------------------------------------------------------
### Paths
###-----------------------------------------------------------------------------

# compute the current directory, the CURDIR variable may be already set
ifndef CURDIR
 CURDIR = $(shell pwd)
endif

# compute the name of the controller from its directory (i.e. braiten)
BAD_NAME = $(basename $(notdir $(CURDIR)))

# this is a hack to work around a problem with spaces in dir names
NAME = $(word $(words $(BAD_NAME)),$(BAD_NAME))

# set jar file name
ifndef JAR_FILE
  JAR_FILE = $(NAME).jar
endif

###-----------------------------------------------------------------------------
### Compilation options
###-----------------------------------------------------------------------------

# set the Java compiler options
JAVA_VER = 11

ifeq ($(OSTYPE),windows)
  SEP = ;
  XLINT = -Xlint
else
  SEP = :
  XLINT = -Xlint:-options -Xlint
endif

WEBOTS_CONTROLLER_JAR = $(WEBOTS_CONTROLLER_LIB_PATH)/java/Controller.jar
LIB = ../lib
SIMLEJOS_JAR = $(LIB)/simlejos.jar
# add additional JARs here and to the classpath below to link them at compile time

ifneq ($(strip $(CLASSPATH)),)
  CLASSPATH_OPT = "$(WEBOTS_CONTROLLER_JAR)$(SEP)$(SIMLEJOS_JAR)$(SEP)$(CLASSPATH)$(SEP)."
else
  CLASSPATH_OPT = "$(WEBOTS_CONTROLLER_JAR)$(SEP)$(SIMLEJOS_JAR)$(SEP)."
endif

JAVAC_OPTS += $(XLINT) -classpath $(CLASSPATH_OPT) -target $(JAVA_VER) -source $(JAVA_VER)

###-----------------------------------------------------------------------------
### JUnit test variables
###-----------------------------------------------------------------------------

JU_PCSA = $(LIB)/junit-platform-console-standalone-1.7.0-all.jar
JU_PR = $(LIB)/junit-platform-runner-1.7.0.jar
JU_PSA = $(LIB)/junit-platform-suite-api-1.7.0.jar

###-----------------------------------------------------------------------------
### Makefile setup and rules
###-----------------------------------------------------------------------------

.PHONY: all clean release debug profile run test

$(JAR_FILE): clean
	@javac $(JAVAC_OPTS) *.java
	@jar cf $@ *.class

jar: clean $(JAR_FILE)

run: jar
	@echo "To run the controller from the Makefile, make sure your robot controller is set to <extern> in the Webots world and the simulation is not already running."
	@java -cp $(SIMLEJOS_JAR)$(SEP)$(WEBOTS_CONTROLLER_JAR)$(SEP). $(NAME)

clean:
	@rm -rf *.jar *.class */*.class */*/*.class */*/*/*.class */*/*/*/*.class > /dev/null 2>&1 || :

test:
	@javac -cp $(WEBOTS_CONTROLLER_JAR)$(SEP)$(SIMLEJOS_JAR)$(SEP)$(JU_PCSA)$(SEP)$(JU_PR)$(SEP)$(JU_PSA)$(SEP). ca/mcgill/ecse211/*/*.java
	@java -jar $(JU_PCSA) -cp $(WEBOTS_CONTROLLER_JAR)$(SEP)$(SIMLEJOS_JAR)$(SEP)$(JU_PCSA)$(SEP)$(JU_PR)$(SEP)$(JU_PSA)$(SEP). -p ca.mcgill.ecse211.test | $(LIB)/ansi2html.sh > test_results_`date +"%Y-%m-%d_%H%M"`.html

javadoc: jar
	@javadoc -d doc -sourcepath . -subpackages ca -cp $(WEBOTS_CONTROLLER_JAR)$(SEP)$(SIMLEJOS_JAR)$(SEP)$(JU_PCSA)$(SEP)$(JU_PR)$(SEP)$(JU_PSA)$(SEP).
