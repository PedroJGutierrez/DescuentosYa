import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.proyecto.Descuentosya.components.DataManager
import com.proyecto.Descuentosya.components.Billetera
import com.proyecto.Descuentosya.ui.theme.BannerCard

@Composable
fun ListaBanners(context: Context) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(DataManager.billeteras) { billetera: Billetera ->
            BannerCard(billetera = billetera, context = context)
        }
    }
}
