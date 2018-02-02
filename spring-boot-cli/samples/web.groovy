@Controller
class Example {

	@Autowired
	private MyService myService

	@RequestMapping("/")
	@ResponseBody
    String helloWorld() {
		return myService.sayWorld()
    }

}

@Service
class MyService {

	String sayWorld() {
		return "World!"
    }
}
