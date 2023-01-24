<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="ISO-8859-1"/>
    <title>Crypto Tool</title>
    <style>
        .underline {
            text-decoration: underline;
        }
        .container {
            font-family: arial, serif;
            margin: 25px;
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
        }
        .child {
            padding: 25px;
            outline: dashed 1px black;
        }
        li {
          padding: 10px;
        }
        p {
            text-align: end;
        }
    </style>

</head>
<body>
<div class="container">
    <div class="child">
    <h1>Crypto Tool</h1>
    <p>created by Adrian Wild</p>
    <h5 class="underline">In this tool you can encrypt or decrypt texts.</h5>
    <ul>
        <li>To encrypt, type in the text you want to encrypt, then a password and finally press the encrypt button.<br>
        The cipher will be shown in the output field.</li>
        <li>To decrypt, type in the cipher you want to decrypt, then the corresponding password and finally press the decrypt button.<br>
            The plain text will be displayed in the output field.</li>
        <li>To reset all fields press the reset button.</li>
    </ul>
    <br>
    <br>

    <form action="crypto" method="post">
        <h3 class="underline">Input</h3>
        <textarea id="inputId" name="input" rows="10" cols="100" placeholder="enter text..."></textarea>
        <h3 class="underline">Password</h3>
        <div>Umlauts aren't allowed for the password</div>
        <input id="passwordId" type="password" name="password" onkeyup="checkEmptyField()" placeholder="enter password...">
        <h3 class="underline">Action</h3>
        <input id="encodeId" type="submit" name="method" value="encrypt" disabled>
        <input id="decodeId" type="submit" name="method" value="decrypt" disabled onclick="checkSuccess()">
        <input id="resetId" type="submit" name="method" value="reset">
    </form>

    <script type="text/javascript">

        <% String error = (String) request.getAttribute("error");
            if (error != null && !error.equals("")) {
                out.print("alert('" + error + "')");
            }
        %>
        function checkEmptyField() {
            console.log("value: " + document.getElementById("passwordId").value)
            const passwordLength = document.getElementById("passwordId").value.length
            if (passwordLength === 0 || passwordLength > 16 || document.getElementById("inputId").value === "" ) {
                document.getElementById("encodeId").disabled = true
                document.getElementById("decodeId").disabled = true
            } else {
                document.getElementById("encodeId").disabled = false
                document.getElementById("decodeId").disabled = false
            }
        }
    </script>
    <br>
    <br>
    <h3 class="underline">Output</h3>
    <textarea rows="10" cols="100" readonly><%
        if(request.getAttribute("output") == null) {
            out.print("");
        } else {
            Object output = request.getAttribute("output");
            out.print(output);
        }
    %>
    </textarea>
    </div>
</div>
</body>
</html>