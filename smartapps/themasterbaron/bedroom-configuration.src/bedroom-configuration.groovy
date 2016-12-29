/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Unlock It When I Arrive
 *
 *  Author: SmartThings
 *  Date: 2013-02-11
 */

definition(
    name: "Bedroom Configuration",
    namespace: "TheMasterBaron",
    author: "TheMasterBaron",
    description: "Configures The Bedroom",
    category: "Safety & Security",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402x.png",
    oauth: true
)

preferences {
	section("Bedroom") {
		input "people", "capability.presenceSensor", title: "Bed Sensors", multiple: true, required: true
		input "tv", "capability.switch", title: "TV", required: true
		input "noise", "capability.switch", title: "Noise Machines", required: true
	}
}

def installed()
{
	subscribe(noise, "switch", noiseHandler)
	subscribe(tv, "switch", tvHandler)
	subscribe(people, "presence", presenceHandler)
	log.debug "installed"
    poll()
}

def updated()
{
	unsubscribe()
	subscribe(noise, "switch", noiseHandler)
	subscribe(tv, "switch", tvHandler)
	subscribe(people, "presence", presenceHandler)
	log.debug "updated"
}

def presenceHandler(evt)
{
	log.debug "presenceHandler: ${evt.value} & ${everyoneIsAway()}"
    poll()
}

def tvHandler(evt)
{
	log.debug "tvHandler: ${evt.value}"
    poll()
}

def noiseHandler(evt)
{
	log.debug "noiseHandler: ${evt.value}"
    poll()
}

def isNoiseOn() {
	return noise.currentState("switch").getValue() == 'on'
}

def isTVOn() {
	return tv.currentState("switch").getValue() == 'on'
}

// returns true if all configured sensors are not present,
// false otherwise.
private everyoneIsAway() {
    def result = true
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) {
        if (person.currentPresence == "present") {
            // someone is present, so set our our result
            // variable to false and terminate the loop.
            result = false
            break
        }
    }
    log.debug "everyoneIsAway: $result"
    return result
}

def poll() {
	if(!tv || !people || !noise) {
    	return 
    }
    
    if(everyoneIsAway() || isTVOn()) {
	    if( isNoiseOn() ) {
    		log.debug "turn noise off"
      		noise.off()
        }
    } else if(!isNoiseOn()) {
    	log.debug "turn noise on"
      	noise.on()
    }
}
