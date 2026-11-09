const url = "http://localhost:8080/api/transfers";

const username = "user";
const password = process.env.LEDGERLINE_BASIC_PASSWORD;

if (!password) {
    throw new Error("Set LEDGERLINE_BASIC_PASSWORD first");
}

const auth = Buffer.from(`${username}:${password}`).toString("base64");

const requestBody = {
    senderAccountNumber: "LL4D40A87F798A",
    receiverAccountNumber: "LLE1FB87D05D51",
    amount: 100,
    idempotencyKey: "concurrent-test-001"
};

async function sendTransfer(requestNumber) {
    try {
        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Basic ${auth}`
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();

        console.log(`Request ${requestNumber}:`, response.status, data);
    } catch (error) {
        console.log(`Request ${requestNumber} failed:`, error.message);
    }
}

async function runTest() {
    console.log("Starting simultaneous transfer requests...");

    await Promise.all([
        sendTransfer(1),
        sendTransfer(2)
    ]);

    console.log("Concurrency test completed.");
}

runTest();