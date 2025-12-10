package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @Verify
    public void testVerifyNoArgs() {
    }
    
    @Verify
    public void testWillError() throws Exception {
        throw new Exception("this must fail");
    }

    @VerifyLong(output={})
    public void testWillFailBecauseNoOutput() {
    }

    @VerifyLong(input={""}, output={})
    public void testWillFailBecauseInconsistent() {
    }

    @VerifyLong(input={"15"}, output={15})
    public long testWillSucceedBecauseCorrect(String input) {
        return Long.valueOf(input);
    }

    @VerifyLong(input={"15"}, output={15})
    public long testWillFailBecauseIncorrent(String input) {
        return Long.valueOf(input) + 15;
    }

    @VerifyDouble(input={"3.1415"}, output={3.1415})
    public Double testWithDouble(String input) {
        return Double.valueOf(input);
    }

    @VerifyString(input={"hello world"}, output={"bye world"})
    public String testWithString(String input) {
        return input.replace("hello", "bye");
    }

    /*

Testing class: uj.wmii.pwj.anns.MyBeautifulTestSuite
====================================
▗▄▄▄▖▗▄▄▄▖ ▗▄▄▖▗▄▄▄▖▗▄▄▄▖▗▖  ▗▖ ▗▄▄▖
  █  ▐▌   ▐▌     █    █  ▐▛▚▖▐▌▐▌
  █  ▐▛▀▀▘ ▝▀▚▖  █    █  ▐▌ ▝▜▌▐▌▝▜▌
  █  ▐▙▄▄▖▗▄▄▞▘  █  ▗▄█▄▖▐▌  ▐▌▝▚▄▞▘


▗▄▄▄▖▗▖  ▗▖ ▗▄▄▖▗▄▄▄▖▗▖  ▗▖▗▄▄▄▖
▐▌   ▐▛▚▖▐▌▐▌     █  ▐▛▚▖▐▌▐▌
▐▛▀▀▘▐▌ ▝▜▌▐▌▝▜▌  █  ▐▌ ▝▜▌▐▛▀▀▘    v3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679
▐▙▄▄▖▐▌  ▐▌▝▚▄▞▘▗▄█▄▖▐▌  ▐▌▐▙▄▄▖
====================================

[===] Running 8 tests
[*1] testWillFailBecauseNoOutput
    ERROR: empty output; use @Verify
[ 2] testVerifyNoArgs
    PASS
[ 3] testWillError
    PASS
[*4] testWillFailBecauseInconsistent
    ERROR: output length does not match input
[ 5] testWillSucceedBecauseCorrect
    PASS
[*6] testWillFailBecauseIncorrent
    FAIL: invalid answer: expected 15, got 30
[ 7] testWithDouble
    PASS
[ 8] testWithString
    PASS
[===   Tests completed: 8
[      PASS:            5
[      FAIL:            1
[      ERROR:           2
    
    */
}
