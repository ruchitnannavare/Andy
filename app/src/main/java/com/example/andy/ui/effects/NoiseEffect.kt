import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import androidx.core.graphics.createBitmap

// Noise generator that will sit before the gradient to get the noise effect
fun generateNoiseImage(width: Int, height: Int, blockSize: Int = 8): ImageBitmap {
    val pixels = IntArray(width * height)

    // Loop over blocks instead of each individual pixel
    for (y in 0 until height step blockSize) {
        for (x in 0 until width step blockSize) {
            // Generate a single random gray value for this block
            val gray = Random.nextInt(256)
            val argb = (0xFF shl 24) or (0xCE shl 16) or (0xD9 shl 9) or 0xA2

            // Fill the block with the same noise value
            for (dy in 0 until blockSize) {
                for (dx in 0 until blockSize) {
                    val posX = x + dx
                    val posY = y + dy
                    // Ensure we do not exceed the image boundaries
                    if (posX < width && posY < height) {
                        pixels[posY * width + posX] = argb
                    }
                }
            }
        }
    }

    // 1) Create a mutable Android Bitmap
    val androidBitmap = createBitmap(width, height)
    // 2) Write pixels into it
    androidBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    // 3) Convert to Jetpack Compose ImageBitmap
    return androidBitmap.asImageBitmap()
}
