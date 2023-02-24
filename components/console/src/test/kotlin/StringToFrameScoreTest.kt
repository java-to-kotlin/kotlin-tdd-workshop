import bowling.console.FrameScore
import bowling.console.toFrameScore
import io.kotest.core.spec.style.AnnotationSpec
import kotlin.test.assertTrue

class StringToFrameScoreTest : AnnotationSpec() {
    @Test
    fun `open frames`() {
        assertTrue("1,0,1".toFrameScore() == FrameScore(1,0,1))
        assertTrue("2,5,7".toFrameScore() == FrameScore(2,5,7))
    }
    
    @Test
    fun `numeric spare`() {
        assertTrue("3,7,10".toFrameScore() == FrameScore(3,7,10))
        assertTrue("6,4,20".toFrameScore() == FrameScore(6,4,20))
    }
    
    @Test
    fun `symbolic spare`() {
        assertTrue("3,/,10".toFrameScore() == FrameScore(3,7,10))
        assertTrue("6,/,20".toFrameScore() == FrameScore(6,4,20))
    }
    
    @Test
    fun `numeric strike`() {
        assertTrue("10,,20".toFrameScore() == FrameScore(10,null,20))
    }
    
    @Test
    fun `symbolic strike`() {
        assertTrue(",X,21".toFrameScore() == FrameScore(10,null,21))
        assertTrue("X,,21".toFrameScore() == FrameScore(10,null,21))
    }
    
    @Test
    fun `incomplete frame`() {
        assertTrue(",,".toFrameScore() == FrameScore(null,null,null))
        assertTrue(",,7".toFrameScore() == FrameScore(null,null,7))
        assertTrue(",2,7".toFrameScore() == FrameScore(null,2,7))
        assertTrue("2,,7".toFrameScore() == FrameScore(2,null,7))
        assertTrue("2,,".toFrameScore() == FrameScore(2,null,null))
    }
}
