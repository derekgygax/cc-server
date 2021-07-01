with open('matches.txt') as f, open('update_match_ids.sql','w') as wf:
    for l in f:
        hc, ac = l.strip('\r\n').split()

        p1 = int(hc.split('-')[0], 16)
        p3 = int(ac.split('-')[0], 16)
        if p1 < p3:
            match_id = '-'.join([hc, ac])
        else:
            match_id = '-'.join([ac, hc])
        update_query = 'update Matches set match_id="{match_id}" where home_couple_id="{hc}" and away_couple_id="{ac}";'\
            .format(hc=hc, ac=ac, match_id=match_id)
        wf.write(update_query + '\n')
