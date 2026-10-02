package com.imshahid.gitauto.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imshahid.gitauto.ui.theme.*

enum class Screen { Home, Transactions, Add, Budgets, Reports, Ai, Goals, Profile }
data class Tx(val title:String,val subtitle:String,val amount:String,val income:Boolean=false)
private val txs=listOf(Tx("Groceries","Today, 10:24 AM","-$45.00"),Tx("Bus Ticket","Today, 08:15 AM","-$2.50"),Tx("Salary","Today, 07:00 AM","+$2,500.00",true),Tx("Online Shopping","Yesterday, 09:20 PM","-$60.00"),Tx("Electricity Bill","Yesterday, 06:20 PM","-$30.00"))

@Composable fun XpnseTrackApp(){
 var screen by remember{mutableStateOf(Screen.Home)}
 Scaffold(
  bottomBar={if(screen !in listOf(Screen.Add,Screen.Ai,Screen.Reports,Screen.Goals)) BottomBar(screen){screen=it}},
  floatingActionButton={if(screen !in listOf(Screen.Add,Screen.Ai,Screen.Reports,Screen.Goals)) FloatingActionButton(onClick={screen=Screen.Add}){Icon(Icons.Default.Add,null)}}
 ){pad->Box(Modifier.padding(pad).fillMaxSize()){when(screen){
  Screen.Home->Home{screen=it};Screen.Transactions->Transactions();Screen.Add->AddExpense{screen=Screen.Home};Screen.Budgets->Budgets();Screen.Reports->Reports();Screen.Ai->AiAssistant();Screen.Goals->Goals();Screen.Profile->Profile{screen=it}
 }}}
}
@Composable private fun BottomBar(current:Screen,onSelect:(Screen)->Unit){NavigationBar{listOf(Screen.Home to Icons.Default.Home,Screen.Transactions to Icons.Default.ReceiptLong,Screen.Budgets to Icons.Default.AccountBalanceWallet,Screen.Profile to Icons.Default.Person).forEach{(s,i)->NavigationBarItem(current==s,{onSelect(s)},{Icon(i,null)},label={Text(s.name)})}}}
@Composable private fun Responsive(content:@Composable ColumnScope.()->Unit){Box(Modifier.fillMaxSize()){Column(Modifier.widthIn(max=720.dp).fillMaxHeight().align(Alignment.TopCenter).verticalScroll(rememberScrollState()).padding(horizontal=20.dp,vertical=16.dp),content=content)}}
@Composable private fun Section(t:String,modifier:Modifier=Modifier){Text(t,fontWeight=FontWeight.Bold,fontSize=18.sp,modifier=modifier.padding(top=22.dp,bottom=10.dp))}
@Composable private fun Metric(title:String,value:String,color:Color,modifier:Modifier=Modifier){Card(modifier){Column(Modifier.padding(14.dp)){Text(title);Text(value,color=color,fontWeight=FontWeight.Bold)}}}
@Composable private fun TxRow(t:Tx){ListItem(headlineContent={Text(t.title,fontWeight=FontWeight.SemiBold)},supportingContent={Text(t.subtitle)},leadingContent={Icon(Icons.Default.Receipt,null)},trailingContent={Text(t.amount,color=if(t.income) Income else Expense,fontWeight=FontWeight.Bold)})}

@Composable private fun Home(go:(Screen)->Unit)=Responsive{
 Row(verticalAlignment=Alignment.CenterVertically){Icon(Icons.Default.Eco,null,tint=Brand);Spacer(Modifier.width(8.dp));Text("XpnseTrack",fontWeight=FontWeight.Bold,fontSize=20.sp);Spacer(Modifier.weight(1f));IconButton({}){Icon(Icons.Default.NotificationsNone,null)}}
 Spacer(Modifier.height(16.dp));Card(colors=CardDefaults.cardColors(containerColor=Brand),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(20.dp)){Text("Total Balance",color=Color.White.copy(.8f));Text("$ 1,250.00",color=Color.White,fontSize=32.sp,fontWeight=FontWeight.Bold)}}
 Spacer(Modifier.height(12.dp));Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(12.dp)){Metric("Income","$ 2,500.00",Income,Modifier.weight(1f));Metric("Expenses","$ 1,250.00",Expense,Modifier.weight(1f))}
 Section("Spending overview");Card(Modifier.fillMaxWidth()){Column(Modifier.padding(20.dp)){Text("$1,250",fontSize=28.sp,fontWeight=FontWeight.Bold);Text("Total spent this month");Spacer(Modifier.height(12.dp));listOf("Food 35%","Transport 20%","Shopping 15%","Bills 10%","Others 20%").forEach{Text("•  "+it,Modifier.padding(vertical=3.dp))}}}
 Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically){Section("Recent transactions",Modifier.weight(1f));TextButton({go(Screen.Transactions)}){Text("See all")}};txs.take(3).forEach{TxRow(it)}
 Section("Smart tools");LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(listOf(Screen.Reports,Screen.Ai,Screen.Goals)){s->AssistChip(onClick={go(s)},label={Text(if(s==Screen.Ai)"AI Assistant" else s.name)})}}
}
@Composable private fun Transactions()=Responsive{
 Text("Transactions",fontSize=28.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(14.dp));OutlinedTextField("",{},Modifier.fillMaxWidth(),placeholder={Text("Search transactions…")},leadingIcon={Icon(Icons.Default.Search,null)});Spacer(Modifier.height(12.dp))
 Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){FilterChip(true,{}, {Text("All")});FilterChip(false,{}, {Text("Income")});FilterChip(false,{}, {Text("Expense")})};Section("Recent");txs.forEach{TxRow(it)}
}
@Composable private fun AddExpense(back:()->Unit)=Responsive{
 Row(verticalAlignment=Alignment.CenterVertically){IconButton(back){Icon(Icons.Default.ArrowBack,null)};Text("Add Expense",fontSize=26.sp,fontWeight=FontWeight.Bold)};Spacer(Modifier.height(12.dp));Text("$ 25.00",fontSize=38.sp,fontWeight=FontWeight.Bold,modifier=Modifier.align(Alignment.CenterHorizontally))
 Section("Category");LazyRow(horizontalArrangement=Arrangement.spacedBy(8.dp)){items(listOf("Food","Transport","Shopping","Bills","Fun","Other")){AssistChip(onClick={},label={Text(it)})}}
 var title by remember{mutableStateOf("")};var note by remember{mutableStateOf("")};OutlinedTextField(title,{title=it},Modifier.fillMaxWidth(),label={Text("Title")},placeholder={Text("Lunch at Cafe")});Spacer(Modifier.height(10.dp));OutlinedTextField(note,{note=it},Modifier.fillMaxWidth(),label={Text("Note (optional)")});Spacer(Modifier.height(18.dp));Button(back,Modifier.fillMaxWidth().height(52.dp)){Text("Save expense")}
}
@Composable private fun Budgets()=Responsive{
 Text("Budgets",fontSize=28.sp,fontWeight=FontWeight.Bold);Section("Monthly limits")
 listOf("Food" to .70f,"Transport" to .40f,"Shopping" to .40f,"Bills" to .45f,"Entertainment" to .33f,"Others" to .50f).forEach{(n,p)->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(16.dp)){Row{Text(n,fontWeight=FontWeight.Bold);Spacer(Modifier.weight(1f));Text(((p*100).toInt()).toString()+"%")};Spacer(Modifier.height(8.dp));LinearProgressIndicator(progress={p},modifier=Modifier.fillMaxWidth())}}}
}
@Composable private fun Reports()=Responsive{
 Text("Reports & Analytics",fontSize=28.sp,fontWeight=FontWeight.Bold);Text("$1,250",fontSize=36.sp,fontWeight=FontWeight.Bold,modifier=Modifier.padding(top=30.dp));Text("Total spent · This month");Section("Category breakdown");listOf("Food 35% · $437.50","Transport 20% · $250.00","Shopping 15% · $187.50","Bills 10% · $125.00","Others 20% · $250.00").forEach{ListItem(headlineContent={Text(it)},leadingContent={Icon(Icons.Default.Circle,null,tint=Brand)})}
}
@Composable private fun AiAssistant()=Responsive{
 Text("AI Assistant",fontSize=28.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(12.dp));Card{Text("Hi! Ask about spending, budgets, trends, or financial goals.",Modifier.padding(18.dp))};Section("Try asking")
 listOf("Analyze my spending this month","Where am I overspending?","Create a budget plan","Compare this month vs last month","Give me savings tips").forEach{OutlinedButton({},Modifier.fillMaxWidth().padding(vertical=4.dp)){Text(it)}};Spacer(Modifier.height(20.dp));OutlinedTextField("",{},Modifier.fillMaxWidth(),placeholder={Text("Ask anything…")},trailingIcon={Icon(Icons.Default.Send,null)})
}
@Composable private fun Goals()=Responsive{
 Text("Goals / Savings",fontSize=28.sp,fontWeight=FontWeight.Bold);Section("Your goals");listOf(Triple("New Laptop","$400 / $1,200",.33f),Triple("Vacation Trip","$600 / $2,000",.30f),Triple("Emergency Fund","$1,000 / $5,000",.20f),Triple("New Phone","$300 / $1,000",.30f)).forEach{(a,b,p)->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(16.dp)){Text(a,fontWeight=FontWeight.Bold);Text(b);Spacer(Modifier.height(8.dp));LinearProgressIndicator(progress={p},modifier=Modifier.fillMaxWidth())}}}
}
@Composable private fun Profile(go:(Screen)->Unit)=Responsive{
 Text("Profile",fontSize=28.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(18.dp));Row(verticalAlignment=Alignment.CenterVertically){Surface(shape=CircleShape,color=MaterialTheme.colorScheme.surfaceVariant,modifier=Modifier.size(56.dp)){Icon(Icons.Default.Person,null,Modifier.padding(12.dp))};Spacer(Modifier.width(12.dp));Column{Text("Shahid Abdullah",fontWeight=FontWeight.Bold);Text("Account settings",color=MaterialTheme.colorScheme.onSurfaceVariant)}}
 Section("Settings");listOf("Edit Profile","Currency · USD ($)","Notifications","Theme · System","Backup & Sync","Connected Accounts","Help & Support","About").forEach{ListItem(headlineContent={Text(it)},trailingContent={Icon(Icons.Default.ChevronRight,null)})};Section("More");OutlinedButton({go(Screen.Reports)},Modifier.fillMaxWidth()){Text("Reports & analytics")};OutlinedButton({go(Screen.Ai)},Modifier.fillMaxWidth()){Text("AI Assistant")};OutlinedButton({go(Screen.Goals)},Modifier.fillMaxWidth()){Text("Savings goals")}
}
