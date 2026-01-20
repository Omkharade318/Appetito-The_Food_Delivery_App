package com.example.appetito.ui.features.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appetito.R
import com.example.appetito.ui.FoodHubTextField
import com.example.appetito.ui.GroupSocialButtons
import com.example.appetito.ui.theme.Orange

@Composable
fun SignUpScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        var name by remember {
            mutableStateOf("")
        }

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }


        Image(
            painter = painterResource(id = R.drawable.auth_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.sign_up),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(20.dp))

            FoodHubTextField(
                value = name,
                onValueChange = {name = it},
                label = {
                    Text(
                        text = stringResource(id = R.string.full_name),
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            FoodHubTextField(
                value = email,
                onValueChange = {email = it},
                label = {
                    Text(
                        text = stringResource(id = R.string.email),
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            FoodHubTextField(
                value = password,
                onValueChange = {password = it},
                label = {
                    Text(text = stringResource(
                        id = R.string.password),
                        color = Color.Gray,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_eye),
                        contentDescription = null,
                        tint = Color.LightGray
                    )
                }
            )

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up),
                    modifier = Modifier
                        .padding(horizontal = 48.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

            }


            Spacer(modifier = Modifier.size(16.dp))

            Text(
                text = stringResource(id = R.string.already_have_account),
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { }
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(16.dp))

            GroupSocialButtons(
                onFacebookClick = { },
                onGoogleClick = { },
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSignUpScreen(){
    SignUpScreen()
}