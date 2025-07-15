package ru.chantreck.myapplication

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import ru.chantreck.myapplication.model.*
import ru.chantreck.myapplication.ui.components.*
import ru.chantreck.myapplication.ui.screens.*
import ru.chantreck.myapplication.interpreter.*
import ru.chantreck.myapplication.ui.theme.*
import java.io.File

enum class AppScreen {
    SPLASH, WELCOME, MAIN
}

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val PREFS_NAME = "app_preferences"
        private const val KEY_DARK_THEME = "dark_theme"

        const val DEBUG_MODE = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (DEBUG_MODE) Log.d(TAG, "onCreate() called")

        val savedDarkTheme = loadThemePreference()

        setContent {
            var isDarkTheme by remember { mutableStateOf(savedDarkTheme) }
            var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }
            var currentProject by remember { mutableStateOf<String?>(null) }
            var currentTab by remember { mutableStateOf(Tab.MAIN) }
            var editingBlock by remember { mutableStateOf<CodeBlockData?>(null) }
            var hasError by remember { mutableStateOf(false) }

            //это получается у нас для мин ширины экрана
            val configuration = LocalConfiguration.current
            val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp }
            val minWidth = Dimens.Max800
            val actualWidth = maxOf(screenWidth, minWidth)

            LaunchedEffect(isDarkTheme) {
                saveThemePreference(isDarkTheme)
            }

            Theme(darkTheme = isDarkTheme) {
                val blocks = remember { mutableStateListOf<CodeBlockData>() }
                val variables = remember { mutableStateMapOf<String, VariableData>() }
                var output by remember { mutableStateOf("") }
                val context = LocalContext.current

                val interpreter = remember { SimpleInterpreter(context) }

                val onRunCode: () -> Unit = {
                    try {
                        output = executeCode(blocks, variables, interpreter)
                    } catch (e: Exception) {
                        Log.e(TAG, "Code execution failed", e)
                        output = "Execution error: ${e.message}"
                    }
                }

                val onResetExecution: () -> Unit = {
                    output = ""
                    hasError = false
                }

                val onExportToTxt: () -> Unit = {
                    exportToTxt(blocks, variables, output)
                }

                val onShowError: (String) -> Unit = { message ->
                    output = "Error: $message"
                    hasError = true
                }

                val onArrayCreated: () -> Unit = {
                    hasError = false
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .width(actualWidth)
                        .background(
                            if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFFFFBFE)
                        )
                ) {
                    when (currentScreen) {
                        AppScreen.SPLASH -> SplashScreen(
                            onSplashFinished = {
                                if (DEBUG_MODE) Log.d(TAG, "Splash finished, moving to WELCOME")
                                currentScreen = AppScreen.WELCOME
                            }
                        )

                        AppScreen.WELCOME -> WelcomeScreen(
                            onContinue = { projectName ->
                                if (DEBUG_MODE) Log.d(TAG, "Welcome finished, project: $projectName")
                                currentProject = projectName
                                currentScreen = AppScreen.MAIN
                            }
                        )

                        AppScreen.MAIN -> {
                            MainScreenWithDrawer(
                                isDarkTheme = isDarkTheme,
                                onSwitchTheme = { isDarkTheme = !isDarkTheme },
                                projectName = currentProject ?: "Project",
                                onExportToTxt = onExportToTxt,
                                currentTab = currentTab,
                                onTabSelected = { currentTab = it },
                                hasError = hasError,
                                blocks = blocks,
                                variables = variables,
                                output = output,
                                onRunCode = onRunCode,
                                onResetExecution = onResetExecution,
                                onBlockCreate = { newBlock -> blocks.add(newBlock) },
                                onBlockMove = { id, x, y ->
                                    blocks.find { it.id == id }?.let { block ->
                                        block.mutableOffsetX = x
                                        block.mutableOffsetY = y
                                    }
                                },
                                onDeleteBlock = { block -> blocks.remove(block) },
                                onEditBlock = { editingBlock = it },
                                onUpdateBlockText = { newText, block ->
                                    val index = blocks.indexOfFirst { it.id == block.id }
                                    if (index != -1) {
                                        blocks[index] = blocks[index].copy(text = newText)
                                    }
                                },
                                onConnectBlocks = { from, to ->
                                    from.connectedTo.add(to.id)
                                },
                                onUpdateVariables = { newVars ->
                                    variables.clear()
                                    variables.putAll(newVars)
                                },
                                onShowError = onShowError,
                                onArrayCreated = onArrayCreated
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadThemePreference(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_THEME, true)
    }

    private fun saveThemePreference(isDarkTheme: Boolean) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .apply()
    }

    private fun executeCode(
        blocks: List<CodeBlockData>,
        variables: Map<String, VariableData>,
        interpreter: SimpleInterpreter
    ): String {
        val sortedBlocks = blocks.sortedBy { it.mutableOffsetY }
        return interpreter.execute(sortedBlocks, variables)
    }

    private fun exportToTxt(
        blocks: List<CodeBlockData>,
        variables: Map<String, VariableData>,
        output: String
    ) {
        try {
            val content = buildString {
                append("Code Export\n")
                append("=============\n\n")
                append("Blocks:\n")
                blocks.forEach { block ->
                    append("${block.text}\n")
                }
                append("\nVariables:\n")
                variables.forEach { (name, variable) ->
                    append("$name (${variable.type}) = ${variable.value}\n")
                }
                append("\nOutput:\n")
                append(output)
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileName = "code_export_${System.currentTimeMillis()}.txt"
            val file = File(downloadsDir, fileName)
            file.writeText(content)

            if (DEBUG_MODE) {
                Log.d(TAG, "File exported: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Export failed", e)
            throw e
        }
    }

    override fun onStart() {
        super.onStart()
        if (DEBUG_MODE) Log.d(TAG, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        if (DEBUG_MODE) Log.d(TAG, "onResume()")
    }

    override fun onPause() {
        super.onPause()
        if (DEBUG_MODE) Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        if (DEBUG_MODE) Log.d(TAG, "onStop()")
    }

    override fun onRestart() {
        super.onRestart()
        if (DEBUG_MODE) Log.d(TAG, "onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (DEBUG_MODE) Log.d(TAG, "onDestroy()")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithDrawer(
    isDarkTheme: Boolean,
    onSwitchTheme: () -> Unit,
    projectName: String,
    onExportToTxt: () -> Unit,
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit,
    hasError: Boolean,
    blocks: MutableList<CodeBlockData>,
    variables: MutableMap<String, VariableData>,
    output: String,
    onRunCode: () -> Unit,
    onResetExecution: () -> Unit,
    onBlockCreate: (CodeBlockData) -> Unit,
    onBlockMove: (String, Float, Float) -> Unit,
    onDeleteBlock: (CodeBlockData) -> Unit,
    onEditBlock: (CodeBlockData?) -> Unit,
    onUpdateBlockText: (String, CodeBlockData) -> Unit,
    onConnectBlocks: (CodeBlockData, CodeBlockData) -> Unit,
    onUpdateVariables: (Map<String, VariableData>) -> Unit,
    onShowError: (String) -> Unit,
    onArrayCreated: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showAboutDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val drawerMenuItems = listOf(
        DrawerMenuItem(
            title = "Clear",
            icon = Icons.Default.Clear
        ) {
            blocks.clear()
            variables.clear()
            onResetExecution()
            scope.launch { drawerState.close() }
        },
        DrawerMenuItem(
            title = if (isDarkTheme) "Day Theme" else "Hight Theme",
            icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode
        ) {
            onSwitchTheme()
            scope.launch { drawerState.close() }
        },
        DrawerMenuItem(
            title = "About",
            icon = Icons.Default.Info
        ) {
            showAboutDialog = true
            scope.launch { drawerState.close() }
        }
    )

    // боковое меню кнопочка эбаут тут такой мини текст по приколу сделала
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "About Project",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Привет! Мы, Настя, Олеся и Лена, создали этот проект, чтобы программирование стало немного прикольнее и красочнее (и чтобы получить много баллов). Тебе нужна помощь или разберешься?",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAboutDialog = false
                        showHelpDialog = true
                    }
                ) {
                    Text("Need your help plz")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAboutDialog = false }
                ) {
                    Text("I'll figure it out!")
                }
            }
        )
    }

    // если типу нужна помощь то вот это вылезет
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "How to Use?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Смотри, у тебя есть два экранчика: editor и run. Сначала составляй из блоков свой код в первом, а затем во втором сможешь перепроверить его, запустить и даже сохранить!",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = { showHelpDialog = false }
                ) {
                    Text("Okay, giiirl!")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                projectName = projectName,
                menuItems = drawerMenuItems,
                isDarkTheme = isDarkTheme,
                blocksCount = blocks.size,
                variablesCount = variables.size
            )
        },
        content = {
            Scaffold(
                topBar = {
                    TopBarWithDrawer(
                        isDarkTheme = isDarkTheme,
                        projectName = projectName,
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        },
                        onExportToTxt = onExportToTxt
                    )
                },
                bottomBar = {
                    BottomTabBar(
                        currentTab = currentTab,
                        onTabSelected = onTabSelected,
                        hasError = hasError
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentTab) {
                        Tab.MAIN -> MainScreen(
                            blocks = blocks,
                            onBlockCreate = onBlockCreate,
                            onBlockMove = onBlockMove,
                            onBlockDrop = { },
                            onDeleteBlock = onDeleteBlock,
                            onEditBlock = onEditBlock,
                            onUpdateBlockText = onUpdateBlockText,
                            onConnectBlocks = onConnectBlocks,
                            variables = variables,
                            onUpdateVariables = onUpdateVariables,
                            onShowError = onShowError,
                            onExportToTxt = onExportToTxt,
                            onArrayCreated = onArrayCreated,
                            modifier = Modifier.fillMaxSize()
                        )

                        Tab.RUN -> RunScreen(
                            blocks = blocks,
                            output = output,
                            variables = variables,
                            onRunCode = onRunCode,
                            onStepExecution = { },
                            onResetExecution = onResetExecution,
                            onBackToEditor = { onTabSelected(Tab.MAIN) },
                            isExecuting = false,
                            currentStep = 0,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithDrawer(
    isDarkTheme: Boolean,
    projectName: String,
    onMenuClick: () -> Unit,
    onExportToTxt: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = projectName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = onExportToTxt) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = "Export",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isDarkTheme) {
                Color(0xFF1E293B)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    )
}

@Composable
fun DrawerContent(
    projectName: String,
    menuItems: List<DrawerMenuItem>,
    isDarkTheme: Boolean,
    blocksCount: Int,
    variablesCount: Int
) {
    ModalDrawerSheet(
        modifier = Modifier.width(Dimens.Max300),
        drawerContainerColor = if (isDarkTheme) {
            Color(0xFF1E293B)
        } else {
            MaterialTheme.colorScheme.surface
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.PaddingMedium)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkTheme) {
                        Color(0xFF334155)
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                ),
                shape = RoundedCornerShape(Dimens.CornerRadius12)
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.PaddingMedium)
                ) {
                    Text(
                        text = projectName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                    Text(
                        text = "Blocks: $blocksCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "Variables: $variablesCount",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Dimens.PaddingLarge))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                items(menuItems) { item ->
                    DrawerMenuItem(
                        item = item,
                        isDarkTheme = isDarkTheme
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DrawerMenuItem(
    item: DrawerMenuItem,
    isDarkTheme: Boolean
) {
    Card(
        onClick = item.onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(Dimens.PaddingSmall)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingSmallMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(Dimens.PaddingLarge)
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDarkTheme) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
