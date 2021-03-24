package digital.capsa.query.services

import digital.capsa.query.model.member.Member
import digital.capsa.query.repo.MemberRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MemberService(private val repository: MemberRepository) {

    fun getMember(memberId: UUID): Member {
        return repository.getOne(memberId)
    }

    fun getMemberList(): List<Member> {
        return repository.findAll()
    }

    fun registerMember(member: Member) {
        if (repository.existsById(member.memberId)) {
            throw Error("Member with ${member.memberId} already exist")
        }
        repository.save(member)
    }
}