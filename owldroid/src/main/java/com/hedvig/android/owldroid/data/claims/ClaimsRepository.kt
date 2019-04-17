package com.hedvig.android.owldroid.data.claims

import com.apollographql.apollo.ApolloClient
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.type.HedvigColor
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClaimsRepository @Inject constructor(private val apolloClient: ApolloClient) {
    private lateinit var claimsQuery: CommonClaimQuery

//    fun fetchCommonClaims(): Observable<CommonClaimQuery.Data?> {
//        claimsQuery = CommonClaimQuery
//            .builder()
//            .build()
//
//        return Rx2Apollo
//            .from(apolloClient.query(claimsQuery).watcher())
//            .map { it.data() }
//    }

    fun fetchCommonClaims(): Observable<CommonClaimQuery.Data?> =
        Observable.just(
            CommonClaimQuery.Data(
                listOf(
                    CommonClaimQuery.CommonClaim(
                        "Emergency",
                        "Det är kris!",
                        CommonClaimQuery.Icon("", "/app-content-service/warning.xml", "", ""),
                        CommonClaimQuery.AsEmergency(
                            "Emergency",
                            HedvigColor.PINK,
                            "Vill du prata med någon kan vi ringa upp dig. Behöver du akutvård utomlands måste du först kontakta Hedvig Global Assistance."
                        )
                    )/*,
                    CommonClaimQuery.CommonClaim(
                        "TitleBulletPoint",
                        "Försenat bagage",
                        CommonClaimQuery.Icon("", "", "", ""),
                        CommonClaimQuery.AsTitleAndBulletPoints(
                            "BulletPoint",
                            listOf(
                                CommonClaimQuery.BulletPoint(
                                    "type",
                                    "1 500 kr per dygn och väska, högst 4 000 kr totalt ",
                                    "Ersättning",
                                    CommonClaimQuery.Icon1("", "")
                                ),
                                CommonClaimQuery.BulletPoint(
                                    "type",
                                    "1 500 kr per dygn och väska, högst 4 000 kr totalt ",
                                    "Ersättning",
                                    CommonClaimQuery.Icon1("", "")
                                )
                            ),
                            "Anmäl försenat bagage",
                            "Blir ditt bagage försenat när du flyger ersätter Hedvig dig för att du ska kunna köpa saker du behöver. Hur mycket du får beror på hur mycket bagaget är försenat.",
                            HedvigColor.LIGHTGRAY,
                            CommonClaimQuery.Icon2("", ""),
                            "Försenat bagage"
                        )
                    )*/
                )
            )
        )
}

