import de.rdvsb.kmutil.LineFrom
import de.rdvsb.kmutil.logMessage
import de.rdvsb.kmutil.pipeSystem
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal class KmProcJvmKtTest {

	@Test
	fun runBinary() {
		println("KmProcJvmKtTest.runBinary start")


		println("KmProcJvmKtTest.runBinary stop")
	}

	@Test
	fun system() {
		println("KmProcJvmKtTest.system start")

		de.rdvsb.kmutil.system("ls; sleep 1;", 300.toDuration(DurationUnit.SECONDS)) { line, lineFrom, process ->
			when (lineFrom) {
				LineFrom.OUT -> logMessage('I', "  ls> $line")
				LineFrom.ERR -> logMessage('E', "  ls> $line")
				LineFrom.EOP -> logMessage('I', "  ls terminates with exit value ${process.exitValue()}")
			}
		}

		println("KmProcJvmKtTest.system stop")
	}

	@Test
	fun pipeSystem() {
		println("KmProcJvmKtTest.pipeSystem start")

		runBlocking {
			var exitCode = -1

			pipeSystem("grep XXX", 300.toDuration(DurationUnit.SECONDS)) { line, lineFrom, process ->
				when (lineFrom) {
					LineFrom.OUT -> logMessage('I', "  grep> $line")
					LineFrom.ERR -> logMessage('E', "  grep> $line")
					LineFrom.EOP -> {
						logMessage('I', "  grep terminates with exit value ${process.exitValue()}")
						exitCode = process.exitValue()
						assertEquals(exitCode, 0, "expected grep match exit 0")
					}
				}
			}.bufferedWriter().use {
				it.write("ABC\n")
				it.write("XXXabc\n")
				it.flush()
				it.write("abc\n")
			}

			pipeSystem("grep XXX", 300.toDuration(DurationUnit.SECONDS)) { line, lineFrom, process ->
				when (lineFrom) {
					LineFrom.OUT -> logMessage('I', "  grep> $line")
					LineFrom.ERR -> logMessage('E', "  grep> $line")
					LineFrom.EOP -> {
						logMessage('I', "  grep no match terminates with exit value ${process.exitValue()}")
						exitCode = process.exitValue()
						assertEquals(exitCode, 1, "expected grep no match exit 1")
					}
				}
			}.bufferedWriter().use {
				it.write("ABC\n")
				it.write("XXabc\n")
				it.flush()
				it.write("abc\n")
			}
		}



		println("KmProcJvmKtTest.pipeSystem stop")
	}
}