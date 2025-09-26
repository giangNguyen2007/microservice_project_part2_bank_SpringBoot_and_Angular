
import {test_getAllUser_shouldFail, testLogin_ok, testLogin_wrongPass} from "./testGateway.js";
import {test_createAccount, test_createTransaction} from "./testGateway_account.js";


async function runTests ( testFunction ) {

    console.log( `\x1b[33m ********* START TEST FOR ${testFunction.name} ************* \x1b[0m \n` );

    try {
        await testFunction()

        console.log("✅=============== TEST SUCCESS ===============\n");

    } catch (err) {
        console.log("error 3")
        console.error(err.message);
        console.log(`❌=============== TEST FAILED FOR ${testFunction.name} ===============\n`);
    }

}

// await runTests(testLogin_ok);
// await runTests(testLogin_wrongPass);
// await runTests(test_getAllUser_shouldFail);


// await runTests(test_createAccount);

await runTests(test_createTransaction)