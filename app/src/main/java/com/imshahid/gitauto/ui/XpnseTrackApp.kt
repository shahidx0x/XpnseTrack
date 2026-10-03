package com.imshahid.gitauto.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imshahid.gitauto.ui.theme.Brand
import com.imshahid.gitauto.ui.theme.Expense
import com.imshahid.gitauto.ui.theme.Income
import kotlinx.coroutines.delay

enum class Screen {
    Splash, Onboarding1, Onboarding2, Onboarding3,
    SignIn, SignUp, ForgotPassword,
    Home, Transactions, TransactionDetails, AddTransaction,
    Budgets, BudgetEditor, Reports, Ai, ReceiptScanner,
    Goals, GoalEditor, Profile, EditProfile, NotificationSettings,
    BackupSync, ConnectedAccounts, HelpSupport, About
}

data class Tx(val title: String, val subtitle: String, val amount: String, val income: Boolean = false)

private val txs = listOf(
    Tx("Groceries", "Today, 10:24 AM", "-$45.00"),
    Tx("Bus Ticket", "Today, 08:15 AM", "-$2.50"),
    Tx("Salary", "Today, 07:00 AM", "+$2,500.00", true),
    Tx("Online Shopping", "Yesterday, 09:20 PM", "-$60.00"),
    Tx("Electricity Bill", "Yesterday, 06:20 PM", "-$30.00")
)

private val mainScreens = setOf(Screen.Home, Screen.Transactions, Screen.Budgets, Screen.Profile)

@Composable
fun XpnseTrackApp() {
    var screen by remember { mutableStateOf(Screen.Splash) }
    var showExit by remember { mutableStateOf(false) }

    fun go(target: Screen) { screen = target }
    fun back() {
        screen = when (screen) {
            Screen.Onboarding2 -> Screen.Onboarding1
            Screen.Onboarding3 -> Screen.Onboarding2
            Screen.SignUp, Screen.ForgotPassword -> Screen.SignIn
            Screen.TransactionDetails, Screen.AddTransaction -> Screen.Transactions
            Screen.BudgetEditor -> Screen.Budgets
            Screen.Reports, Screen.Ai, Screen.ReceiptScanner, Screen.Goals -> Screen.Home
            Screen.GoalEditor -> Screen.Goals
            Screen.EditProfile, Screen.NotificationSettings, Screen.BackupSync,
            Screen.ConnectedAccounts, Screen.HelpSupport, Screen.About -> Screen.Profile
            else -> Screen.Home
        }
    }

    BackHandler(enabled = screen != Screen.Splash) {
        if (screen in mainScreens) showExit = true else back()
    }

    Scaffold(
        bottomBar = { if (screen in mainScreens) AppNavigationBar(screen, ::go) },
        floatingActionButton = {
            if (screen in mainScreens) {
                FloatingActionButton(onClick = { go(Screen.AddTransaction) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (screen) {
                Screen.Splash -> SplashScreen { go(Screen.Onboarding1) }
                Screen.Onboarding1 -> OnboardingScreen(Icons.Default.PieChart, "Take Control of Your Expenses", "Track spending, set budgets and understand your money.", "Next", "Skip", { go(Screen.Onboarding2) }, { go(Screen.SignIn) })
                Screen.Onboarding2 -> OnboardingScreen(Icons.Default.ReceiptLong, "Smart Tracking", "Categorize transactions, scan receipts and stay organized.", "Next", "Back", { go(Screen.Onboarding3) }, { go(Screen.Onboarding1) })
                Screen.Onboarding3 -> OnboardingScreen(Icons.Default.AutoAwesome, "Your AI Financial Assistant", "Ask questions, analyze habits and get contextual insights.", "Get Started", "Back", { go(Screen.SignIn) }, { go(Screen.Onboarding2) })
                Screen.SignIn -> SignInScreen({ go(Screen.Home) }, { go(Screen.SignUp) }, { go(Screen.ForgotPassword) })
                Screen.SignUp -> SignUpScreen({ go(Screen.Home) }, ::back)
                Screen.ForgotPassword -> ForgotPasswordScreen(::back)
                Screen.Home -> HomeScreen(::go)
                Screen.Transactions -> TransactionsScreen({ go(Screen.TransactionDetails) }, { go(Screen.AddTransaction) })
                Screen.TransactionDetails -> TransactionDetailsScreen(::back)
                Screen.AddTransaction -> AddTransactionScreen(::back) { go(Screen.Transactions) }
                Screen.Budgets -> BudgetsScreen { go(Screen.BudgetEditor) }
                Screen.BudgetEditor -> BudgetEditorScreen(::back) { go(Screen.Budgets) }
                Screen.Reports -> ReportsScreen(::back)
                Screen.Ai -> AiAssistantScreen(::back)
                Screen.ReceiptScanner -> ReceiptScannerScreen(::back)
                Screen.Goals -> GoalsScreen(::back) { go(Screen.GoalEditor) }
                Screen.GoalEditor -> GoalEditorScreen(::back) { go(Screen.Goals) }
                Screen.Profile -> ProfileScreen(::go)
                Screen.EditProfile -> EditProfileScreen(::back)
                Screen.NotificationSettings -> NotificationSettingsScreen(::back)
                Screen.BackupSync -> BackupSyncScreen(::back)
                Screen.ConnectedAccounts -> ConnectedAccountsScreen(::back)
                Screen.HelpSupport -> HelpSupportScreen(::back)
                Screen.About -> AboutScreen(::back)
            }
        }
    }

    if (showExit) {
        ConfirmDialog("Exit XpnseTrack?", "Your saved data will remain available next time.", "Exit", { showExit = false }, { showExit = false })
    }
}

@Composable
private fun AppNavigationBar(current: Screen, go: (Screen) -> Unit) {
    NavigationBar {
        listOf(
            Triple(Screen.Home, "Home", Icons.Default.Home),
            Triple(Screen.Transactions, "Transactions", Icons.Default.ReceiptLong),
            Triple(Screen.Budgets, "Budgets", Icons.Default.AccountBalanceWallet),
            Triple(Screen.Profile, "Profile", Icons.Default.Person)
        ).forEach { (screen, label, icon) ->
            NavigationBarItem(
                selected = current == screen,
                onClick = { go(screen) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) }
            )
        }
    }
}

@Composable
private fun ResponsivePage(title: String? = null, onBack: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val horizontal = if (maxWidth < 600.dp) 16.dp else 28.dp
        val contentMax = if (maxWidth < 900.dp) 720.dp else 840.dp
        Column(
            Modifier.widthIn(max = contentMax).fillMaxHeight().align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState()).padding(horizontal = horizontal, vertical = 12.dp)
        ) {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                    Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }
            content()
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(900); onDone() }
    Box(Modifier.fillMaxSize().background(Brand), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.16f), modifier = Modifier.size(88.dp)) {
                Icon(Icons.Default.Eco, null, tint = Color.White, modifier = Modifier.padding(22.dp))
            }
            Spacer(Modifier.height(18.dp))
            Text("XpnseTrack", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
            Text("Track Today. Build a Better Tomorrow.", color = Color.White.copy(alpha = 0.82f))
        }
    }
}

@Composable
private fun OnboardingScreen(icon: ImageVector, title: String, body: String, button: String, secondary: String, onPrimary: () -> Unit, onSecondary: () -> Unit) = ResponsivePage {
    Spacer(Modifier.height(40.dp))
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(148.dp).align(Alignment.CenterHorizontally)) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(38.dp))
    }
    Spacer(Modifier.height(32.dp))
    Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(10.dp))
    Text(body, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(34.dp))
    Button(onClick = onPrimary, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text(button) }
    TextButton(onClick = onSecondary, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(secondary) }
}

@Composable
private fun SignInScreen(onSignIn: () -> Unit, onSignUp: () -> Unit, onForgot: () -> Unit) = ResponsivePage {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Spacer(Modifier.height(28.dp))
    Icon(Icons.Default.Eco, null, tint = Brand, modifier = Modifier.size(54.dp).align(Alignment.CenterHorizontally))
    Text("Welcome back", fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
    Spacer(Modifier.height(24.dp))
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }, singleLine = true)
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
    TextButton(onClick = onForgot, modifier = Modifier.align(Alignment.End)) { Text("Forgot password?") }
    Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("Sign in") }
    Spacer(Modifier.height(10.dp))
    OutlinedButton(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.AccountCircle, null); Spacer(Modifier.width(8.dp)); Text("Continue with Google") }
    TextButton(onClick = onSignUp, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Create account") }
}

@Composable
private fun SignUpScreen(onDone: () -> Unit, onBack: () -> Unit) = ResponsivePage("Create account", onBack) {
    var name by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }; var password by remember { mutableStateOf("") }
    OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Full name") })
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") })
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
    Spacer(Modifier.height(18.dp))
    Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Create account") }
}

@Composable
private fun ForgotPasswordScreen(onBack: () -> Unit) = ResponsivePage("Reset password", onBack) {
    var email by remember { mutableStateOf("") }; var sent by remember { mutableStateOf(false) }
    Text("Enter your email and we’ll send a reset link.")
    Spacer(Modifier.height(14.dp))
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") })
    Spacer(Modifier.height(14.dp))
    Button(onClick = { sent = true }, modifier = Modifier.fillMaxWidth()) { Text("Send reset link") }
    if (sent) Text("Reset link sent.", color = Income, modifier = Modifier.padding(top = 10.dp))
}

@Composable
private fun HomeScreen(go: (Screen) -> Unit) = ResponsivePage {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Eco, null, tint = Brand); Spacer(Modifier.width(8.dp))
        Text("XpnseTrack", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { go(Screen.NotificationSettings) }) { Icon(Icons.Default.NotificationsNone, null) }
    }
    Spacer(Modifier.height(12.dp))
    Card(colors = CardDefaults.cardColors(containerColor = Brand), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) { Text("Total Balance", color = Color.White.copy(.82f)); Text("$ 1,250.00", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold) }
    }
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MetricCard("Income", "$2,500.00", Income, Modifier.weight(1f))
        MetricCard("Expenses", "$1,250.00", Expense, Modifier.weight(1f))
    }
    SectionTitle("Quick actions")
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        QuickAction("Reports", Icons.Default.BarChart) { go(Screen.Reports) }
        QuickAction("AI", Icons.Default.AutoAwesome) { go(Screen.Ai) }
        QuickAction("Scan", Icons.Default.DocumentScanner) { go(Screen.ReceiptScanner) }
        QuickAction("Goals", Icons.Default.Flag) { go(Screen.Goals) }
    }
    SectionTitle("Recent transactions")
    txs.take(4).forEach { TxRow(it) { go(Screen.TransactionDetails) } }
}

@Composable
private fun TransactionsScreen(onOpen: () -> Unit, onAdd: () -> Unit) = ResponsivePage("Transactions") {
    var query by remember { mutableStateOf("") }; var filter by remember { mutableStateOf("All") }; var sortOpen by remember { mutableStateOf(false) }; var sort by remember { mutableStateOf("Newest") }
    OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth(), placeholder = { Text("Search transactions") }, leadingIcon = { Icon(Icons.Default.Search, null) }, trailingIcon = {
        Box {
            IconButton(onClick = { sortOpen = true }) { Icon(Icons.Default.Sort, null) }
            DropdownMenu(sortOpen, { sortOpen = false }) {
                listOf("Newest", "Oldest", "Highest amount", "Lowest amount").forEach { item ->
                    DropdownMenuItem(text = { Text(item) }, onClick = { sort = item; sortOpen = false })
                }
            }
        }
    })
    Text("Sort: $sort", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 10.dp)) {
        listOf("All", "Income", "Expense").forEach { item -> FilterChip(filter == item, { filter = item }, { Text(item) }) }
    }
    txs.filter { query.isBlank() || it.title.contains(query, true) }.forEach { TxRow(it, onOpen) }
    OutlinedButton(onClick = onAdd, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Add transaction") }
}

@Composable
private fun TransactionDetailsScreen(onBack: () -> Unit) = ResponsivePage("Transaction details", onBack) {
    var showDelete by remember { mutableStateOf(false) }; var moreOpen by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(50.dp)) { Icon(Icons.Default.Restaurant, null, modifier = Modifier.padding(12.dp)) }
        Spacer(Modifier.width(12.dp))
        Column { Text("Groceries", fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("Food · Today 10:24 AM") }
        Spacer(Modifier.weight(1f))
        Box {
            IconButton(onClick = { moreOpen = true }) { Icon(Icons.Default.MoreVert, null) }
            DropdownMenu(moreOpen, { moreOpen = false }) {
                DropdownMenuItem({ Text("Duplicate") }, { moreOpen = false })
                DropdownMenuItem({ Text("Share") }, { moreOpen = false })
                DropdownMenuItem({ Text("Delete") }, { moreOpen = false; showDelete = true })
            }
        }
    }
    Spacer(Modifier.height(18.dp))
    Text("-$45.00", color = Expense, fontSize = 34.sp, fontWeight = FontWeight.Bold)
    SectionTitle("Details")
    DetailRow("Account", "Cash"); DetailRow("Category", "Food"); DetailRow("Date", "Oct 3, 2026"); DetailRow("Note", "Weekly groceries")
    Spacer(Modifier.height(16.dp))
    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Edit transaction") }
    if (showDelete) ConfirmDialog("Delete transaction?", "This action cannot be undone.", "Delete", { showDelete = false }, { showDelete = false })
}

@Composable
private fun AddTransactionScreen(onBack: () -> Unit, onSave: () -> Unit) = ResponsivePage("Add transaction", onBack) {
    var type by remember { mutableStateOf("Expense") }; var amount by remember { mutableStateOf("25.00") }; var title by remember { mutableStateOf("") }; var note by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Food") }; var categoryOpen by remember { mutableStateOf(false) }; var account by remember { mutableStateOf("Cash") }; var accountOpen by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { listOf("Expense", "Income").forEach { item -> FilterChip(type == item, { type = item }, { Text(item) }) } }
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(amount, { amount = it }, Modifier.fillMaxWidth(), label = { Text("Amount") }, prefix = { Text("$ ") })
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Title") })
    Spacer(Modifier.height(10.dp))
    DropdownField("Category", category, categoryOpen, { categoryOpen = it }) {
        listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Other").forEach { item -> DropdownMenuItem({ Text(item) }, { category = item; categoryOpen = false }) }
    }
    Spacer(Modifier.height(10.dp))
    DropdownField("Account", account, accountOpen, { accountOpen = it }) {
        listOf("Cash", "Bank", "Card", "Wallet").forEach { item -> DropdownMenuItem({ Text(item) }, { account = item; accountOpen = false }) }
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(note, { note = it }, Modifier.fillMaxWidth(), label = { Text("Note (optional)") })
    Spacer(Modifier.height(16.dp))
    Button(onClick = onSave, modifier = Modifier.fillMaxWidth().height(48.dp)) { Text("Save $type") }
}

@Composable
private fun BudgetsScreen(onCreate: () -> Unit) = ResponsivePage("Budgets") {
    var monthOpen by remember { mutableStateOf(false) }; var month by remember { mutableStateOf("October") }
    DropdownField("Month", month, monthOpen, { monthOpen = it }) {
        listOf("October", "September", "August").forEach { item -> DropdownMenuItem({ Text(item) }, { month = item; monthOpen = false }) }
    }
    Spacer(Modifier.height(12.dp))
    listOf("Food" to .70f, "Transport" to .40f, "Shopping" to .40f, "Bills" to .45f, "Entertainment" to .33f).forEach { (name, progress) -> BudgetCard(name, progress) }
    OutlinedButton(onClick = onCreate, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Create budget") }
}

@Composable
private fun BudgetEditorScreen(onBack: () -> Unit, onSave: () -> Unit) = ResponsivePage("Budget editor", onBack) {
    var category by remember { mutableStateOf("Food") }; var open by remember { mutableStateOf(false) }; var limit by remember { mutableStateOf("500") }
    DropdownField("Category", category, open, { open = it }) {
        listOf("Food", "Transport", "Shopping", "Bills", "Entertainment", "Other").forEach { item -> DropdownMenuItem({ Text(item) }, { category = item; open = false }) }
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(limit, { limit = it }, Modifier.fillMaxWidth(), label = { Text("Monthly limit") }, prefix = { Text("$ ") })
    Spacer(Modifier.height(14.dp))
    Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) { Text("Save budget") }
}

@Composable
private fun ReportsScreen(onBack: () -> Unit) = ResponsivePage("Reports & analytics", onBack) {
    var period by remember { mutableStateOf("This month") }; var open by remember { mutableStateOf(false) }
    DropdownField("Period", period, open, { open = it }) {
        listOf("This week", "This month", "Last month", "This year").forEach { item -> DropdownMenuItem({ Text(item) }, { period = item; open = false }) }
    }
    Spacer(Modifier.height(12.dp))
    Text("$1,250", fontSize = 34.sp, fontWeight = FontWeight.Bold); Text("Total spent · $period")
    SectionTitle("Category breakdown")
    listOf("Food 35% · $437.50", "Transport 20% · $250.00", "Shopping 15% · $187.50", "Bills 10% · $125.00", "Other 20% · $250.00").forEach {
        ListItem(headlineContent = { Text(it) }, leadingContent = { Icon(Icons.Default.Circle, null, tint = Brand) })
    }
    SectionTitle("Insight")
    Card { Text("Food spending is 12% higher than your previous month.", Modifier.padding(14.dp)) }
}

@Composable
private fun AiAssistantScreen(onBack: () -> Unit) = ResponsivePage("AI Assistant", onBack) {
    var connector by remember { mutableStateOf("Default AI") }; var open by remember { mutableStateOf(false) }; var message by remember { mutableStateOf("") }
    DropdownField("Connector", connector, open, { open = it }) {
        listOf("Default AI", "OpenAI", "Gemini", "Claude", "MCP server").forEach { item -> DropdownMenuItem({ Text(item) }, { connector = item; open = false }) }
    }
    Spacer(Modifier.height(12.dp))
    Card { Text("Ask about spending, budgets, trends or goals. No financial action is executed automatically.", Modifier.padding(14.dp)) }
    SectionTitle("Suggested prompts")
    listOf("Analyze this month", "Where am I overspending?", "Create a budget draft", "Compare with last month").forEach { prompt ->
        AssistChip(onClick = { message = prompt }, label = { Text(prompt) }, modifier = Modifier.padding(end = 6.dp, bottom = 4.dp))
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(message, { message = it }, Modifier.fillMaxWidth(), placeholder = { Text("Ask anything…") }, trailingIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Send, null) } })
}

@Composable
private fun ReceiptScannerScreen(onBack: () -> Unit) = ResponsivePage("Receipt scanner", onBack) {
    var showResult by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().height(320.dp)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.DocumentScanner, null, modifier = Modifier.size(64.dp)); Spacer(Modifier.height(10.dp)); Text("Camera preview"); Text("Align receipt inside the frame", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
    Spacer(Modifier.height(14.dp))
    Button(onClick = { showResult = true }, modifier = Modifier.fillMaxWidth()) { Text("Capture receipt") }
    OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Choose from gallery") }
    if (showResult) {
        AlertDialog(onDismissRequest = { showResult = false }, title = { Text("Receipt detected") }, text = { Text("Coffee House · $10.50\nFood · Oct 3, 2026") }, confirmButton = { TextButton(onClick = { showResult = false }) { Text("Use details") } }, dismissButton = { TextButton(onClick = { showResult = false }) { Text("Retake") } })
    }
}

@Composable
private fun GoalsScreen(onBack: () -> Unit, onCreate: () -> Unit) = ResponsivePage("Goals & savings", onBack) {
    var sortOpen by remember { mutableStateOf(false) }
    Box(Modifier.align(Alignment.End)) {
        TextButton(onClick = { sortOpen = true }) { Text("Sort") }
        DropdownMenu(sortOpen, { sortOpen = false }) { listOf("Priority", "Progress", "Newest").forEach { item -> DropdownMenuItem({ Text(item) }, { sortOpen = false }) } }
    }
    listOf(Triple("New Laptop", "$400 / $1,200", .33f), Triple("Vacation Trip", "$600 / $2,000", .30f), Triple("Emergency Fund", "$1,000 / $5,000", .20f)).forEach { (name, amount, progress) ->
        Card(Modifier.fillMaxWidth().padding(vertical = 5.dp)) { Column(Modifier.padding(14.dp)) { Text(name, fontWeight = FontWeight.Bold); Text(amount); Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth()) } }
    }
    OutlinedButton(onClick = onCreate, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Create goal") }
}

@Composable
private fun GoalEditorScreen(onBack: () -> Unit, onSave: () -> Unit) = ResponsivePage("Goal editor", onBack) {
    var name by remember { mutableStateOf("") }; var target by remember { mutableStateOf("") }
    OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Goal name") })
    Spacer(Modifier.height(10.dp))
    OutlinedTextField(target, { target = it }, Modifier.fillMaxWidth(), label = { Text("Target amount") }, prefix = { Text("$ ") })
    Spacer(Modifier.height(14.dp))
    Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) { Text("Save goal") }
}

@Composable
private fun ProfileScreen(go: (Screen) -> Unit) = ResponsivePage("Profile") {
    var logout by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(58.dp)) { Icon(Icons.Default.Person, null, modifier = Modifier.padding(14.dp)) }
        Spacer(Modifier.width(12.dp))
        Column { Text("Shahid Abdullah", fontWeight = FontWeight.Bold); Text("Account settings", color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
    Spacer(Modifier.height(16.dp))
    SettingsRow("Edit profile", Icons.Default.Edit) { go(Screen.EditProfile) }
    SettingsRow("Notifications", Icons.Default.Notifications) { go(Screen.NotificationSettings) }
    SettingsRow("Backup & sync", Icons.Default.CloudSync) { go(Screen.BackupSync) }
    SettingsRow("Connected accounts", Icons.Default.Hub) { go(Screen.ConnectedAccounts) }
    SettingsRow("Help & support", Icons.Default.HelpOutline) { go(Screen.HelpSupport) }
    SettingsRow("About", Icons.Default.Info) { go(Screen.About) }
    SettingsRow("Reports", Icons.Default.BarChart) { go(Screen.Reports) }
    SettingsRow("AI Assistant", Icons.Default.AutoAwesome) { go(Screen.Ai) }
    TextButton(onClick = { logout = true }, modifier = Modifier.fillMaxWidth()) { Text("Log out", color = Expense) }
    if (logout) ConfirmDialog("Log out?", "You can sign in again at any time.", "Log out", { logout = false }, { logout = false })
}

@Composable
private fun EditProfileScreen(onBack: () -> Unit) = ResponsivePage("Edit profile", onBack) {
    var name by remember { mutableStateOf("Shahid Abdullah") }; var email by remember { mutableStateOf("shahidabdulla.aas@gmail.com") }
    OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Name") }); Spacer(Modifier.height(10.dp))
    OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("Email") }); Spacer(Modifier.height(14.dp))
    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Save changes") }
}

@Composable
private fun NotificationSettingsScreen(onBack: () -> Unit) = ResponsivePage("Notifications", onBack) {
    var budget by remember { mutableStateOf(true) }; var weekly by remember { mutableStateOf(true) }; var reminders by remember { mutableStateOf(false) }
    SwitchRow("Budget alerts", "Notify when nearing a limit", budget) { budget = it }
    SwitchRow("Weekly summary", "Receive a weekly spending summary", weekly) { weekly = it }
    SwitchRow("Expense reminders", "Remind me to log expenses", reminders) { reminders = it }
}

@Composable
private fun BackupSyncScreen(onBack: () -> Unit) = ResponsivePage("Backup & sync", onBack) {
    var confirm by remember { mutableStateOf(false) }
    DetailRow("Status", "Synced"); DetailRow("Last backup", "Just now"); DetailRow("Storage", "Firebase")
    Spacer(Modifier.height(14.dp))
    Button(onClick = { confirm = true }, modifier = Modifier.fillMaxWidth()) { Text("Back up now") }
    if (confirm) ConfirmDialog("Back up now?", "Your local data will be synced to Firebase.", "Back up", { confirm = false }, { confirm = false })
}

@Composable
private fun ConnectedAccountsScreen(onBack: () -> Unit) = ResponsivePage("Connected accounts", onBack) {
    var connectOpen by remember { mutableStateOf<String?>(null) }
    listOf("OpenAI", "Gemini", "Claude", "MCP server").forEach { name ->
        ListItem(headlineContent = { Text(name) }, supportingContent = { Text(if (name == "MCP server") "Custom connector" else "AI connector") }, trailingContent = { TextButton(onClick = { connectOpen = name }) { Text("Connect") } })
    }
    val selected = connectOpen
    if (selected != null) {
        AlertDialog(onDismissRequest = { connectOpen = null }, title = { Text("Connect " + selected) }, text = { Text("Credentials will be stored securely on the backend, not inside the app.") }, confirmButton = { TextButton(onClick = { connectOpen = null }) { Text("Continue") } }, dismissButton = { TextButton(onClick = { connectOpen = null }) { Text("Cancel") } })
    }
}

@Composable
private fun HelpSupportScreen(onBack: () -> Unit) = ResponsivePage("Help & support", onBack) {
    var topicOpen by remember { mutableStateOf(false) }; var topic by remember { mutableStateOf("General") }
    DropdownField("Topic", topic, topicOpen, { topicOpen = it }) {
        listOf("General", "Billing", "Sync", "AI", "Bug report").forEach { item -> DropdownMenuItem({ Text(item) }, { topic = item; topicOpen = false }) }
    }
    Spacer(Modifier.height(10.dp))
    OutlinedTextField("", {}, Modifier.fillMaxWidth(), label = { Text("Describe your issue") }, minLines = 4)
    Spacer(Modifier.height(14.dp))
    Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Send request") }
}

@Composable
private fun AboutScreen(onBack: () -> Unit) = ResponsivePage("About", onBack) {
    Icon(Icons.Default.Eco, null, tint = Brand, modifier = Modifier.size(64.dp).align(Alignment.CenterHorizontally))
    Text("XpnseTrack", fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
    Text("Version 0.2 UI", modifier = Modifier.align(Alignment.CenterHorizontally))
    SectionTitle("Legal")
    ListItem(headlineContent = { Text("Privacy policy") }, trailingContent = { Icon(Icons.Default.ChevronRight, null) })
    ListItem(headlineContent = { Text("Terms of service") }, trailingContent = { Icon(Icons.Default.ChevronRight, null) })
    ListItem(headlineContent = { Text("Open source licenses") }, trailingContent = { Icon(Icons.Default.ChevronRight, null) })
}

@Composable
private fun MetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier) { Column(Modifier.padding(12.dp)) { Text(title); Text(value, color = color, fontWeight = FontWeight.Bold) } }
}

@Composable
private fun QuickAction(label: String, icon: ImageVector, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 8.dp)) {
        Icon(icon, null, modifier = Modifier.size(17.dp)); Spacer(Modifier.width(4.dp)); Text(label, fontSize = 12.sp)
    }
}

@Composable
private fun SectionTitle(text: String) { Text(text, fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp)) }

@Composable
private fun TxRow(tx: Tx, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Receipt, null); Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) { Text(tx.title, fontWeight = FontWeight.SemiBold); Text(tx.subtitle, style = MaterialTheme.typography.bodySmall) }
            Text(tx.amount, color = if (tx.income) Income else Expense, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BudgetCard(name: String, progress: Float) {
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row { Text(name, fontWeight = FontWeight.Bold); Spacer(Modifier.weight(1f)); Text((progress * 100).toInt().toString() + "%") }
            Spacer(Modifier.height(7.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant); Spacer(Modifier.weight(1f)); Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingsRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().padding(vertical = 3.dp), onClick = onClick) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null); Spacer(Modifier.width(12.dp)); Text(label, Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null)
        }
    }
}

@Composable
private fun SwitchRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    ListItem(headlineContent = { Text(title) }, supportingContent = { Text(subtitle) }, trailingContent = { Switch(checked, onCheckedChange = onChecked) })
}

@Composable
private fun DropdownField(label: String, value: String, expanded: Boolean, onExpandedChange: (Boolean) -> Unit, menu: @Composable () -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { onExpandedChange(true) }, modifier = Modifier.fillMaxWidth()) {
            Text(label + ": " + value); Spacer(Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { onExpandedChange(false) }) { menu() }
    }
}

@Composable
private fun ConfirmDialog(title: String, text: String, confirm: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) }, text = { Text(text) }, confirmButton = { TextButton(onClick = onConfirm) { Text(confirm) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
