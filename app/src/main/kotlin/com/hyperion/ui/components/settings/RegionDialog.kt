package com.hyperion.ui.components.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hyperion.R
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegionDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (Locale) -> Unit
) {
    var selectedRegion by remember { mutableStateOf(Locale.US) }

    AlertDialog(
        title = { Text(stringResource(R.string.region)) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 350.dp)
            ) {
                items(Locale.getAvailableLocales()) { locale ->
                    Row(
                        modifier = Modifier.clickable {
                            selectedRegion = locale
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = locale.displayCountry.ifEmpty { locale.displayName },
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(Modifier.weight(1f, true))

                        RadioButton(
                            selected = locale == selectedRegion,
                            onClick = { selectedRegion = locale }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedRegion)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        onDismissRequest = onDismissRequest
    )
}