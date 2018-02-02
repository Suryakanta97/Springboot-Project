package org.test

@Grab("spring-boot-starter-actuator")

@RestController
class SampleController {

	@RequestMapping("/")
    def hello() {
		[message: "Hello World!"]
	}
}
