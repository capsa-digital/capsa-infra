package digital.capsa.archetypes.it.aggregate

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import digital.capsa.archetypes.it.httpRequest
import digital.capsa.it.aggregate.AbstractAggregate
import java.util.Random
import java.util.UUID

class Member(
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var phone: String? = null
) : AbstractAggregate("Member") {

    override fun construct() {
        val index = parent?.getChildCount(Member::class) ?: 0
        val nextInt = random.nextInt(4)
        val gender = PersonMockGenerator.Gender.values()[if (nextInt != 0) 0 else 1]
        firstName = firstName
            ?: PersonMockGenerator.mockFirstName(index = index, gender = gender)
        lastName = lastName
            ?: PersonMockGenerator.mockLastName(index = index)
        email = email
            ?: PersonMockGenerator.mockEmail(index = index, firstName = firstName!!, lastName = lastName)
        phone = phone
            ?: PersonMockGenerator.mockPhone(random = random)
    }

    override fun onCreate() {
        httpRequest("/requests/register-member.json")
            .withTransformation(
                "$.schema" to context.environment.getProperty("api.schema"),
                "$.host" to context.environment.getProperty("api.host"),
                "$.port" to context.environment.getProperty("api.port"),
                "$.body.firstName" to firstName,
                "$.body.lastName" to lastName,
                "$.body.email" to email,
                "$.body.phone" to phone
            )
            .send {
                assertThat(statusCode.value()).isEqualTo(200)
                val ids = ObjectMapper().readTree(body)?.get("ids")
                ids?.also { idsNode ->
                    (idsNode as ArrayNode).forEach { idNode ->
                        when (idNode.fields().next().key) {
                            "memberId" -> id = UUID.fromString(idNode["memberId"].asText())
                        }
                    }
                }
            }
    }

    companion object {
        val random = Random(0)
    }
}