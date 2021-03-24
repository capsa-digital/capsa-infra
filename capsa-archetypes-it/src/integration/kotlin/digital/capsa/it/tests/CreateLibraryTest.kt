package digital.capsa.it.tests

import assertk.assertThat
import assertk.assertions.isEqualTo
import digital.capsa.it.TestContext
import digital.capsa.it.aggregate.Account
import digital.capsa.it.aggregate.Library
import digital.capsa.it.aggregate.Member
import digital.capsa.it.aggregate.account
import digital.capsa.it.aggregate.getChild
import digital.capsa.it.json.OpType
import digital.capsa.it.json.ValidationRule
import digital.capsa.it.json.isJsonWhere
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

@Tag("it")
@RunWith(SpringRunner::class)
@TestPropertySource(locations = ["classpath:application.yml"])
@EnableAutoConfiguration
@SpringBootTest(classes = [IntegrationConfig::class])
@ExtendWith(DataLoader::class)
@DisplayName("Create Library, Members and Books")
class CreateLibraryTest : CapsaApiTestBase() {

    companion object {

        lateinit var demoAccount: Account

        @BeforeAll
        @JvmStatic
        fun createDemoAccount(applicationContext: ApplicationContext) {
            demoAccount = account {
                library {
                    for (i in 1..2) {
                        book { }
                    }
                }
                member {
                    firstName = "John"
                    lastName = "Doe"
                }
                member {
                    phone = "324-222-4567"
                }
                for (i in 1..3) {
                    member { }
                }
            }
            demoAccount.create(TestContext(applicationContext = applicationContext))
        }
    }

    @Test
    fun `verify demo data`() {
        TimeUnit.SECONDS.sleep(5)
        val libraryId = demoAccount.getChild<Library>(0).id
        httpRequest("/requests/get-book-list.json")
                .withTransformation(
                        "$.schema" to appSchema,
                        "$.host" to appHost,
                        "$.port" to appPort,
                        "$.body.libraryId" to libraryId.toString()
                )
                .send {
                    assertThat(statusCode.value()).isEqualTo(200)
                    assertEquals(200, statusCode.value())
                    assertThat(body).isJsonWhere(
                            ValidationRule("\$.bookList.length()", OpType.equal, "{2}")
                    )
                }
        val memberId = demoAccount.getChild<Member>(0).id
        httpRequest("/requests/get-member-details.json")
                .withTransformation(
                        "$.schema" to appSchema,
                        "$.host" to appHost,
                        "$.port" to appPort,
                        "$.path" to "/getMemberDetails/${memberId.toString()}"
                )
                .send {
                    assertEquals(200, statusCode.value())
                    assertThat(body).isJsonWhere(
                            ValidationRule("$.firstName", OpType.equal, "John")
                    )
                }
    }
}