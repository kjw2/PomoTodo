package com.example.pomotodo.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pomotodo.BuildConfig
import com.example.pomotodo.R
import com.example.pomotodo.theme.PomoTodoTheme

@Composable
fun AboutScreen(
  onBackClick: () -> Unit,
  onOpenSourceClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val appVersion = remember { "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})" }

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(18.dp),
  ) {
    item {
      PageHeader(
        title = "정보",
        subtitle = "앱과 배포 정보를 확인합니다.",
        onBackClick = onBackClick,
      )
    }
    item {
      AppIdentitySection(
        appName = stringResource(R.string.app_name),
        version = appVersion,
      )
    }
    item {
      InfoSection(title = "프로그램 정보") {
        InfoRow(label = "프로그램명", value = stringResource(R.string.app_name))
        InfoRow(label = "버전", value = appVersion, testTag = "about_app_version")
        InfoRow(label = "패키지", value = BuildConfig.APPLICATION_ID)
      }
    }
    item {
      InfoSection(title = "개발자 정보") {
        InfoRow(label = "개발자", value = stringResource(R.string.developer_name))
        InfoRow(label = "문의", value = stringResource(R.string.developer_contact))
      }
    }
    item {
      NavigationRow(
        title = "오픈소스 라이선스",
        subtitle = "앱에서 사용하는 주요 오픈소스 구성요소",
        onClick = onOpenSourceClick,
      )
    }
  }
}

@Composable
fun OpenSourceLicensesScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item {
      PageHeader(
        title = "오픈소스 라이선스",
        subtitle = "PomoTodo의 주요 런타임 의존성입니다.",
        onBackClick = onBackClick,
      )
    }
    items(OPEN_SOURCE_LICENSES) { license ->
      LicenseItem(license = license)
    }
  }
}

@Composable
private fun PageHeader(
  title: String,
  subtitle: String,
  onBackClick: () -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      IconButton(onClick = onBackClick) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
      }
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
      }
    }
  }
}

@Composable
private fun AppIdentitySection(
  appName: String,
  version: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape) {
      Icon(
        imageVector = Icons.Filled.Timer,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(14.dp).size(30.dp),
      )
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(text = appName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
      Text(text = "버전 $version", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
  }
}

@Composable
private fun InfoSection(
  title: String,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    Surface(
      modifier = Modifier.fillMaxWidth(),
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
      shape = RoundedCornerShape(8.dp),
    ) {
      Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
        content()
      }
    }
  }
}

@Composable
private fun InfoRow(
  label: String,
  value: String,
  testTag: String? = null,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .then(if (testTag == null) Modifier else Modifier.testTag(testTag))
        .padding(vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = label,
      modifier = Modifier.weight(0.34f),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.bodyMedium,
    )
    Text(
      text = value,
      modifier = Modifier.weight(0.66f),
      style = MaterialTheme.typography.bodyMedium,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun NavigationRow(
  title: String,
  subtitle: String,
  onClick: () -> Unit,
) {
  Surface(
    modifier =
      Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .testTag("open_source_licenses_link"),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
    shape = RoundedCornerShape(8.dp),
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(imageVector = Icons.Filled.Code, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
      }
      Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null)
    }
  }
}

@Composable
private fun LicenseItem(license: OpenSourceLicense) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Icon(imageVector = Icons.Filled.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
      Text(text = license.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
    Text(text = license.description, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
    Text(text = license.license, style = MaterialTheme.typography.bodyMedium)
    Text(text = license.url, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
    Spacer(Modifier.height(4.dp))
    HorizontalDivider()
  }
}

private data class OpenSourceLicense(
  val name: String,
  val description: String,
  val license: String,
  val url: String,
)

private val OPEN_SOURCE_LICENSES =
  listOf(
    OpenSourceLicense(
      name = "AndroidX Core / Activity / Lifecycle",
      description = "Android 앱 호환성, Activity Compose, Lifecycle 연동에 사용합니다.",
      license = "Apache License 2.0",
      url = "https://developer.android.com/jetpack/androidx",
    ),
    OpenSourceLicense(
      name = "Jetpack Compose",
      description = "PomoTodo의 UI 구성과 Material Design 컴포넌트에 사용합니다.",
      license = "Apache License 2.0",
      url = "https://developer.android.com/jetpack/compose",
    ),
    OpenSourceLicense(
      name = "Material Icons Extended",
      description = "앱 내 버튼과 정보 화면 아이콘 표시에 사용합니다.",
      license = "Apache License 2.0",
      url = "https://fonts.google.com/icons",
    ),
    OpenSourceLicense(
      name = "AndroidX Navigation 3",
      description = "Compose 화면 전환과 백스택 관리에 사용합니다.",
      license = "Apache License 2.0",
      url = "https://developer.android.com/guide/navigation",
    ),
    OpenSourceLicense(
      name = "Room",
      description = "할 일 목록을 기기 내 로컬 데이터베이스에 저장하는 데 사용합니다.",
      license = "Apache License 2.0",
      url = "https://developer.android.com/training/data-storage/room",
    ),
    OpenSourceLicense(
      name = "Kotlin / Kotlinx Coroutines",
      description = "앱 로직, 비동기 타이머, 데이터 흐름 처리에 사용합니다.",
      license = "Apache License 2.0",
      url = "https://kotlinlang.org",
    ),
  )

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
  PomoTodoTheme {
    AboutScreen(
      onBackClick = {},
      onOpenSourceClick = {},
      modifier = Modifier.padding(16.dp),
    )
  }
}

@Preview(showBackground = true)
@Composable
fun OpenSourceLicensesScreenPreview() {
  PomoTodoTheme {
    OpenSourceLicensesScreen(onBackClick = {}, modifier = Modifier.padding(16.dp))
  }
}
