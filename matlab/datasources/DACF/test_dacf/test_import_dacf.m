dacf_data = read_dacf_csv('20160808_0000_SN1_D80.UCT');
% dacf_data = read_dacf('20170318_0000_SN6_D80.UCT');

network_data = dacf2matpower(dacf_data);

% write_dacf_excel(dacf_data, 'test_dacf.xlsx');