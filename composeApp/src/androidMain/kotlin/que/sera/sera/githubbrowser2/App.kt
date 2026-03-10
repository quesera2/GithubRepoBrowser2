package que.sera.sera.githubbrowser2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun App(vm: RepoViewModel = viewModel<RepoViewModel>()) {
    val uiState by vm.uiState.collectAsState()
    var username by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("GitHub username") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Button(
                    onClick = { vm.fetchRepos(username) },
                    enabled = username.isNotBlank(),
                ) {
                    Text("検索")
                }
            }

            when (val state = uiState) {
                is RepoUiState.Idle -> Unit
                is RepoUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                is RepoUiState.Error -> Text(
                    text = "エラー: ${state.message}",
                    color = MaterialTheme.colorScheme.error,
                )
                is RepoUiState.Success -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.repos) { repo ->
                        RepoCard(repo)
                    }
                }
            }
        }
    }
}

@Composable
private fun RepoCard(repo: GitHubRepo) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(repo.name, style = MaterialTheme.typography.titleMedium)
            repo.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                repo.language?.let { Text("$it", style = MaterialTheme.typography.labelSmall) }
                Text("★ ${repo.stars}", style = MaterialTheme.typography.labelSmall)
                Text("forks ${repo.forks}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
