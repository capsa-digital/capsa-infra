package digital.capsa.query.services

import digital.capsa.core.aggregates.MemberId
import digital.capsa.query.model.member.Member
import digital.capsa.query.repo.MemberRepository
import org.springframework.stereotype.Component

@Component
class MemberService(private val repository: MemberRepository) {

    fun getMember(memberId: MemberId): Member {
        return repository.getOne(memberId)
    }

    fun getMemberList(): List<Member> {
        return repository.findAll()
    }

    fun registerMember(member: Member) {
        if (repository.existsById(member.id)) {
            throw Error("Member with ${member.id} already exist")
        }
        repository.save(member)
    }
}
